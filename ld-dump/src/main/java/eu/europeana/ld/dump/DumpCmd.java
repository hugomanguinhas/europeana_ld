/**
 * 
 */
package eu.europeana.ld.dump;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.dump.cmd.DatasetHandler;
import eu.europeana.ld.edm.io.TurtleRecordWriter;
import eu.europeana.ld.harvester.LDHarvesterCallback;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.cmd.MongoHarvestCmd;
import eu.europeana.ld.tools.ProgressHarvesterCallback;
import static eu.europeana.ld.tools.ToolkitUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Oct 2016
 */
public class DumpCmd extends MongoHarvestCmd
{
    public static void main(String[] args) { new DumpCmd().process(args); }

    public DumpCmd() { super("/etc/cmd/dump.cfg"); }

    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        addDefaultOptions(options, _props);
        addMongoOptions(options, _props);
        addInputOptions(options, _props); //remove
        addDirOptions(options, _props);
        addFormatsOption(options, _props);
        return options;
    }

    /*
    @Override
    protected void handleCmd(MongoEDMHarvester harvester
                           , LifecycleHarvesterCallback cb
                           , CommandLine line) throws Throwable
    {
        cb.begin();
        try     { harvester.harvestAll(cb); }
        finally { cb.finish();              }
    }
    */

    @Override
    public MongoEDMHarvester getHarvester(CommandLine line, Properties prop)
    {
        MongoClient   c  = getMongoClient(line, prop);
        MongoDatabase db = getMongoDatabase(c, line, prop);
        return new MongoEDMHarvester(c, db, null, true);
    }

    @Override
    protected LDHarvesterCallback getCallback(CommandLine line)
    {
        File           dir = getOutputDirectory(line);
        DatasetHandler dsh = new DatasetHandler(dir, getRDFFormats(line));
        return new ProgressHarvesterCallback(dsh, _ps);
    }

    protected File getOutputDirectory(CommandLine line)
    {
        String dir  = line.getOptionValue("dir");
        File   file = new File(dir);
        if ( !file.isDirectory() ) {
            throw new RuntimeException("Not a directory: " + dir);
        }
        if ( !file.exists() ) { file.mkdirs(); }
        return file;
    }


    public static Options addDirOptions(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder.withArgName("dir")
                .hasArg()
                .withDescription(props.getProperty("info.option.dir"))
                .isRequired()
                .create("dir"));
        return opts;
    }

    public static Options addFormatsOption(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder.withArgName("formats")
                .hasArgs()
                .withValueSeparator(',')
                .withDescription(props.getProperty("info.option.formats"))
                .create("formats"));
        return opts;
    }

    /*
    @Override
    protected void process(CommandLine line) throws Throwable
    {
        checkLogging(line);

        MongoEDMHarvester harv   = getHarvester(line, _props);
        File              out    = getOutputFile(line);
        TurtleIterativeRecordWriter   writer = new TurtleIterativeRecordWriter();
        HarvesterCallback cb     = getCallback(line, writer);

        writer.start(out);
        try                 { handleCmd(harv, cb, line);     }
        finally             { harv.close(); writer.finish(); }
    }

    protected HarvesterCallback getCallback(CommandLine line
            , TurtleIterativeRecordWriter writer)
    {
        return new ProgressHarvesterCallback(new WriterCallback(writer), _ps);
    }

    private class WriterCallback implements HarvesterCallback
    {
        private TurtleIterativeRecordWriter _writer;

        public WriterCallback(TurtleIterativeRecordWriter writer)
        {
            super(_ps); _writer = writer;
        }

        @Override
        public void handle(Resource r, Status status)
        {
            Model m = r.getModel();
            try                  { _writer.write(m);              }
            catch(IOException e) { throw new RuntimeException(e); }
            finally              { m.removeAll();                 }
            printStatus(status);
        }
    };
*/
}