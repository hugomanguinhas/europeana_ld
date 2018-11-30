/**
 * 
 */
package eu.europeana.ld.mongo.cmd;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.util.PrintUtil;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.mongo.MongoHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Oct 2016
 */
public class MongoEntityHarvestCmd extends MongoHarvestCmd
{
    public static void main(String[] args) { new MongoEntityHarvestCmd().process(args); }

    public MongoEntityHarvestCmd()           { this("/etc/cmd/mongo-entity-harvest.cfg"); }
    public MongoEntityHarvestCmd(String cfg) { super(cfg);                         }

    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        addDefaultOptions(options, _props);
        addMongoOptions(options, _props);
        addClassOptions(options, _props);
        addInputOptions(options, _props);
        addOutputOptions(options, _props);
        addRDFFormatOptions(options, _props);
        return options;
    }

    protected static Options addClassOptions(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder.withArgName("class")
                .hasArg()
                .withDescription(props.getProperty("info.option.class"))
                .isRequired()
                .create("class"));
        return opts;
    }

    public MongoHarvester getHarvester(CommandLine line
                                               , Properties prop)
    {
        MongoClient   c  = getMongoClient(line, prop);
        MongoDatabase db = getMongoDatabase(c, line, prop);
        return new MongoEntityHarvester(c, db, getClass(line, prop));
    }

    public Resource getClass(CommandLine line, Properties prop)
    {
        PrintUtil pUtil = new PrintUtil();
        pUtil.registerPrefixMap(EDM.PREFIXES);
        String    cName = line.getOptionValue("class");
        return ResourceFactory.createResource(pUtil.expandQname(cName));
    }
}