/**
 * 
 */
package eu.europeana.ld.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.io.EDMTurtleWriter;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.mongo.MongoEDMHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Oct 2016
 */
public class DumpCmd extends ToolkitCmd
{
    static { loadProperties("/etc/dump/config.xml"); }

    public static void main(String[] args) { new DumpCmd().process(args); }

    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        options.addOption(new Option("help", getProperty("info.option.help")));
        options.addOption(OptionBuilder.withArgName("host")
                .hasArg()
                .withDescription(getProperty("info.option.host"))
                .isRequired()
                .create("host"));
        options.addOption(OptionBuilder.withArgName("port")
                .hasArg()
                .withDescription(getProperty("info.option.port"))
                .withType(Integer.class)
                .create("port"));

        OptionGroup group = new OptionGroup();
        group.setRequired(true);
        group.addOption(OptionBuilder
                .withDescription(getProperty("info.option.all"))
                .create("all"))
             .addOption(OptionBuilder
                .withArgName("uris")
                .hasArgs()
                .withValueSeparator(',')
                .withDescription(getProperty("info.option.uris"))
                .create("uris"))
             .addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription(getProperty("info.option.file"))
                .create("file"))
             .addOption(OptionBuilder
                .withArgName("query")
                .hasArg()
                .withDescription(getProperty("info.option.search"))
                .create("search"));
        options.addOptionGroup(group);

        options.addOption(OptionBuilder.withArgName("out")
                .hasArg()
                .withDescription(getProperty("info.option.file"))
                .isRequired()
                .create("out"));
        return options;
    }

    @Override
    protected void process(CommandLine line) throws Throwable
    {
        MongoEDMHarvester harv   = getHarvester(line);
        File              out    = getOutput(line);
        EDMTurtleWriter   writer = new EDMTurtleWriter();
        HarvesterCallback cb     = getCallback(line, writer);

        writer.start(out);
        try                 { handleCmd(harv, cb, line);     }
        finally             { harv.close(); writer.finish(); }
    }

    protected File getOutput(CommandLine line)
    {
        File file = new File(line.getOptionValue("out"));
        File dir  = file.getParentFile();
        if ( dir != null && !dir.exists() ) { dir.mkdirs(); }
        return file;
    }

    protected MongoEDMHarvester getHarvester(CommandLine line)
    {
        String host   = line.getOptionValue("host");
        String dbName = line.getOptionValue("db", "europeana");
        int    port   = Integer.parseInt(line.getOptionValue("port", "27017"));

        MongoClient   client = new MongoClient(host, port);
        MongoDatabase db     = client.getDatabase(dbName);
        return new MongoEDMHarvester(client, db, null);
    }

    protected HarvesterCallback getCallback(CommandLine line
                                          , EDMTurtleWriter writer)
    {
        PrintStream ps = _ps;
        return new HarvesterCallback()
        {
            private DecimalFormat _df = new DecimalFormat("00.0%");
            private String        _prev = null;

            @Override
            public void handle(Resource r, Status status)
            {
                Model m = r.getModel();
                try                  { writer.write(m);               }
                catch(IOException e) { throw new RuntimeException(e); }
                finally              { m.removeAll();                 }
                printStatus(status);
            }

            private void printStatus(Status status)
            {
                if ( _prev == null ) { ps.print("| Harvesting... "); }

                double percent = (double)status.cursor / status.total;
                String str = _df.format(percent);
                if ( str.equals(_prev) ) { return; }

                if ( _prev != null ) { ps.print("\b\b\b\b\b"); }
                _prev = str;
                ps.print(str);
            }
        };
    }

    protected void handleCmd(MongoEDMHarvester harvester, HarvesterCallback cb
                           , CommandLine line) throws Throwable
    {
        if ( line.hasOption("all") ) { harvester.harvestAll(cb); return; }

        if ( line.hasOption("uris") ) {
            List<String> uris = Arrays.asList(line.getOptionValues("uris"));
            harvester.harvest(uris, cb);
            return;
        }

        if ( line.hasOption("file") ) {
            File         file = new File(line.getOptionValue("file"));
            List<String> uris = FileUtils.readLines(file);
            harvester.harvest(uris, cb);
            return;
        }

        if ( line.hasOption("search") ) {
            harvester.harvestBySearch(line.getOptionValue("search"), cb);
            return;
        }
    }
}