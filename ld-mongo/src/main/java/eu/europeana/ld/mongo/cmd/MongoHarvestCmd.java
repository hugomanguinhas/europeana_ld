/**
 * 
 */
package eu.europeana.ld.mongo.cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.io.EDMDatasetFileNaming;
import eu.europeana.ld.io.FileNaming.URLEncodeFileNaming;
import eu.europeana.ld.edm.io.TurtleRecordWriter;
import eu.europeana.ld.edm.io.XMLIterativeResourceWriter;
import eu.europeana.ld.edm.io.XMLRecordWriter;
import eu.europeana.ld.harvester.LDHarvester;
import eu.europeana.ld.harvester.LDHarvesterCallback;
import eu.europeana.ld.harvester.WriterCallback;
import eu.europeana.ld.io.FileNaming;
import eu.europeana.ld.jena.DefaultWriter;
import eu.europeana.ld.jena.IterativeDefaultWriter;
import eu.europeana.ld.jena.IterativeRecordWriter;
import eu.europeana.ld.jena.JSONLDWriter;
import eu.europeana.ld.jena.RecordWriter;
import eu.europeana.ld.jena.ZipRecordWriter;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.MongoHarvester;
import eu.europeana.ld.tools.ProgressHarvesterCallback;
import eu.europeana.ld.tools.ToolkitCmd;
import static eu.europeana.ld.tools.ToolkitUtils.*;
import static eu.europeana.ld.edm.EDM.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Oct 2016
 */
public class MongoHarvestCmd extends ToolkitCmd
{
    public static void main(String[] args) { new MongoHarvestCmd().process(args); }
    
    public MongoHarvestCmd()           { this("/etc/cmd/mongo-harvest.cfg"); }
    public MongoHarvestCmd(String cfg) { super(cfg);                         }

    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        addDefaultOptions(options, _props);
        addMongoOptions(options, _props);
        addFixIRIOption(options, _props);
        addEDMOption(options, _props);
        addInputOptions(options, _props);
        addOutputOptions(options, _props);
        addRDFFormatOptions(options, _props);
        return options;
    }

    @Override
    protected void process(CommandLine line) throws Throwable
    {
        checkLogging(line);

        MongoHarvester harv = getHarvester(line, _props);
        try     { handleCmd(harv, getCallback(line), line); }
        finally { harv.close();                             }
    }

    protected void checkLogging(CommandLine line)
    {
        if ( !line.hasOption("silent") ) { return; }
        Logger.getLogger(LDHarvester.class.getName()).setLevel(Level.OFF);
    }

    protected void handleCmd(MongoHarvester harvester
                           , LDHarvesterCallback cb
                           , CommandLine line) throws Throwable
    {
        cb.begin();
        try {
            if ( line.hasOption("all") ) { harvester.harvestAll(cb); return; }
    
            if ( line.hasOption("uris") ) {
                harvester.harvest(getURIs(line), cb);
                return;
            }
    
            if ( line.hasOption("file") ) {
                File         file = new File(line.getOptionValue("file"));
                List<String> uris = FileUtils.readLines(file);
                harvester.harvest(uris, cb);
                return;
            }

            if ( line.hasOption("datasets") ) {
                harvester.harvestBySearch(getDatasetQuery(line), cb);
                return;
            }

            if ( line.hasOption("search") ) {
                harvester.harvestBySearch(line.getOptionValue("search"), cb);
                return;
            }
        }
        finally { cb.finish(); }
    }

    public static MongoClient getMongoClient(CommandLine line
                                           , Properties prop)
    {
        String defPort = prop.getProperty("defaults.port");
        String host = line.getOptionValue("host");
        int    port = Integer.parseInt(line.getOptionValue("port", defPort));
        return new MongoClient(host, port);
    }

    public static MongoDatabase getMongoDatabase(MongoClient c, CommandLine line
                                               , Properties prop)
    {
        String defDb = prop.getProperty("defaults.col");
        return c.getDatabase(line.getOptionValue("col", defDb));
    }

    public MongoHarvester getHarvester(CommandLine line
                                     , Properties prop)
    {
        MongoClient   c  = getMongoClient(line, prop);
        MongoDatabase db = getMongoDatabase(c, line, prop);
        Resource      cz = getClass(line, prop);
        return new MongoEDMHarvester(c, db, cz, false, getFixIRIs(line));
    }

    private boolean getFixIRIs(CommandLine line)
    {
        return line.hasOption("fixIRIs");
    }

    public Resource getClass(CommandLine line, Properties prop)
    {
        String name = line.getOptionValue("class");
        return (name == null ? null : EDM.getResourceByQName(name));
    }

    private List<String> getURIs(CommandLine line)
    {
        String[] uris = line.getOptionValues("uris");
        List<String> list = new ArrayList(uris.length);
        for ( String uri : uris )
        {
            if ( uri.trim().isEmpty() ) { continue; }
            list.add(uri);
        }
        return list;
    }

    private String getDatasetQuery(CommandLine line)
    {
        String[] datasets = line.getOptionValues("datasets");
        String   str      = null;
        for ( String dataset : datasets )
        {
            if ( dataset.trim().isEmpty() ) { continue; }

            str = (str == null ? "" : str + "|") + "(" + dataset + ")";
        }
        return "{'about': { $regex: '^/" + str + "/.*' }}";
    }


    /***************************************************************************
     * Protected Methods - Output
     **************************************************************************/

    protected OutputStream getOutput(CommandLine line)
    {
        try { return new FileOutputStream(getOutputFile(line)); }
        catch (FileNotFoundException e) { throw new RuntimeException(e); }
    }

    protected LDHarvesterCallback getCallback(CommandLine line)
    {
        ResourceCallback  cb = getCallback(line, line.getOptionValue("out"));
        return new ProgressHarvesterCallback(cb, _ps);
    }

    protected ResourceCallback getCallback(CommandLine line, String out)
    {
        int i = out.lastIndexOf('.');
        if ( i < 0 ) { return getDirCallback(line, getRDFFormat(line)); }

        String ext = out.substring(i+1);
        if ( ext.equals("zip") ) { return getZipCallback(line);  }
        if ( ext.equals("gz" ) ) { return getGZipCallback(line); }

        Lang lang = getRDFFormat(ext);
        return getFileCallback(line, lang);
    }

    protected ResourceCallback getDirCallback(CommandLine line, Lang lang)
    {
        File         dir    = getOutputDir(line);
        FileNaming   naming = getFileNaming(line);
        RecordWriter writer = getWriter(lang);
        return new ResourceCallback<Resource>() {

            @Override
            public void handle(String id, Resource r, Status s)
            {
                try {
                    String fn = naming.convert(id, lang);

                    writer.write(r, new FileOutputStream(new File(dir, fn)));
                }
                catch (IOException e) { e.printStackTrace();      }
                finally               { r.getModel().removeAll(); }
            }
        };
    }

    protected ResourceCallback getFileCallback(CommandLine line, Lang lang)
    {
        return new WriterCallback(getOutput(line)
                                , getIterativeWriter(lang));
    }

    protected ResourceCallback getGZipCallback(CommandLine line) 
    {
        Lang lang = getRDFFormat(line);
        if ( lang == null ) {
            lang = getRDFFormatFromFilename(line.getOptionValue("out"));
        }

        OutputStream out = null;
        try { out = new GZIPOutputStream(getOutput(line), 65536); }
        catch (IOException e) { throw new RuntimeException(e); }

        return new WriterCallback(out, getIterativeWriter(lang));
    }

    protected ResourceCallback getZipCallback(CommandLine line)
    {
        Lang lang = getRDFFormat(line);
        if ( lang == null ) { throw new RuntimeException("Unsupported RDF format"); }

        ZipRecordWriter writer = new ZipRecordWriter(getWriter(lang), lang
                                                   , getFileNaming(line)
                                                   , true);
        return new WriterCallback(getOutput(line), writer);
    }

    protected FileNaming getFileNaming(CommandLine line)
    {
        Resource r = getClass(line, _props);
        return ( (r == null || EDM.ProvidedCHO.equals(r))
               ? new EDMDatasetFileNaming()
               : new URLEncodeFileNaming() );
    }

    protected RecordWriter getWriter(Lang lang)
    {
        if ( lang == null        ) { 
            throw new RuntimeException("Unknown RDF format");
        }
        if ( lang == Lang.JSONLD ) {
            return new JSONLDWriter(ENTITY_JSONLD_CONTEXT);
        }
        if ( lang == Lang.RDFXML ) {
            return new XMLRecordWriter();
        }

        return new DefaultWriter(lang);
    }

    protected IterativeRecordWriter getIterativeWriter(Lang lang)
    {
        if ( lang == null ) { throw new RuntimeException("Unknown RDF format");}

        if ( lang == Lang.RDFXML ) { return new XMLIterativeResourceWriter();  }
        if ( lang == Lang.TURTLE ) { return new TurtleRecordWriter(); }
        return new IterativeDefaultWriter(lang);

      //throw new RuntimeException("Unsupported RDF format: " + lang.getLabel());
    }


    /***************************************************************************
     * Protected Methods - Options
     **************************************************************************/

    public static Options addMongoOptions(Options opts, Properties prop)
    {
        return opts.addOption(OptionBuilder.withArgName("host")
                .hasArg()
                .withDescription(prop.getProperty("info.option.host"))
                .isRequired()
                .create("host"))
            .addOption(OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription(prop.getProperty("info.option.port"))
                .withType(Integer.class)
                .create("port"))
            .addOption(OptionBuilder.withArgName("col")
                .hasArg()
                .withDescription(prop.getProperty("info.option.col"))
                .create("col"));
    }

    public static Options addEDMOption(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder.withArgName("class")
                .hasArg()
                .withDescription(props.getProperty("info.option.class"))
                .create("class"));
        return opts;
    }

    public static Options addInputOptions(Options opts, Properties prop)
    {
        OptionGroup group = new OptionGroup()
            .addOption(OptionBuilder
                .withDescription(prop.getProperty("info.option.all"))
                .create("all"))
            .addOption(OptionBuilder
                .withArgName("uris")
                .hasArgs()
                .withValueSeparator(',')
                .withDescription(prop.getProperty("info.option.uris"))
                .create("uris"))
            .addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription(prop.getProperty("info.option.file"))
                .create("file"))
            .addOption(OptionBuilder
                .withArgName("query")
                .hasArg()
                .withDescription(prop.getProperty("info.option.search"))
                .create("search"))
            .addOption(OptionBuilder
                .withArgName("datasets")
                .hasArg()
                .withType(Integer.class)
                .withDescription(prop.getProperty("info.option.datasets"))
                .create("datasets"));
        group.setRequired(true);
        opts.addOptionGroup(group);
        return opts;
    }

    public static Options addRDFFormatOptions(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder.withArgName("format")
                .hasArg()
                .withDescription(props.getProperty("info.option.format"))
                .create("format"));
        return opts;
    }

    public static Options addFixIRIOption(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder
                .withDescription(props.getProperty("info.option.fixIRI"))
                .create("fixIRI"));
        return opts;
    }
}