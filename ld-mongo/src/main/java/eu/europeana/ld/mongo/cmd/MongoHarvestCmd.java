/**
 * 
 */
package eu.europeana.ld.mongo.cmd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.io.EDMTurtleWriter;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.harvester.LDHarvester;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.tools.ToolkitCmd;

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
        addOutputOptions(options, _props);

        OptionGroup group = new OptionGroup()
            .addOption(OptionBuilder
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
        group.setRequired(true);
        options.addOptionGroup(group);

        return options;
    }

    @Override
    protected void process(CommandLine line) throws Throwable
    {
    }

    protected void checkLogging(CommandLine line)
    {
        if ( !line.hasOption("silent") ) { return; }
        Logger.getLogger(LDHarvester.class.getName()).setLevel(Level.OFF);
    }

    protected void handleCmd(MongoEDMHarvester harvester, HarvesterCallback cb
                           , CommandLine line) throws Throwable
    {
        _ps.print("| Harvesting... ");
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
        String defDb = prop.getProperty("defaults.db");
        return c.getDatabase(line.getOptionValue("db", defDb));
    }

    public static MongoEDMHarvester getHarvester(CommandLine line
                                               , Properties prop)
    {
        MongoClient   c  = getMongoClient(line, prop);
        MongoDatabase db = getMongoDatabase(c, line, prop);
        return new MongoEDMHarvester(c, db, null);
    }
}