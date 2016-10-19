/**
 * 
 */
package eu.europeana.ld.dump;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.edm.io.EDMTurtleWriter;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.cmd.MongoHarvestCmd;
import eu.europeana.ld.tools.ProgressHarvesterCallback;

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
        checkLogging(line);

        MongoEDMHarvester harv   = getHarvester(line, _props);
        File              out    = getOutput(line);
        EDMTurtleWriter   writer = new EDMTurtleWriter();
        HarvesterCallback cb     = getCallback(line, writer);

        writer.start(out);
        try                 { handleCmd(harv, cb, line);     }
        finally             { harv.close(); writer.finish(); }
    }

    protected HarvesterCallback getCallback(CommandLine line
            , EDMTurtleWriter writer)
    {
        return new WriterCallback(writer);
    }

    protected void handleCmd(MongoEDMHarvester harvester, HarvesterCallback cb
                           , CommandLine line) throws Throwable
    {
        harvester.harvestAll(cb);
    }

    private class WriterCallback extends ProgressHarvesterCallback
    {
        private EDMTurtleWriter _writer;

        public WriterCallback(EDMTurtleWriter writer)
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

}