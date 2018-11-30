/**
 * 
 */
package eu.europeana.ld.dump;

import java.io.File;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.jena.riot.Lang;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.dump.cmd.ChunkHandler;
import eu.europeana.ld.harvester.LDHarvesterCallback;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.cmd.MongoHarvestCmd;
import eu.europeana.ld.tools.ProgressHarvesterCallback;

import static eu.europeana.ld.dump.cmd.ChunkHandler.*;
import static eu.europeana.ld.tools.ToolkitUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Oct 2016
 */
public class VirtuosoDumpCmd extends MongoHarvestCmd
{
    public static void main(String[] args) { new VirtuosoDumpCmd().process(args); }

    public VirtuosoDumpCmd() { super("/etc/cmd/virtuoso.cfg"); }

    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        addDefaultOptions(options, _props);
        addMongoOptions(options, _props);
        addInputOptions(options, _props);
        addOutputOptions(options, _props);
        addRDFFormatOptions(options, _props);
        addChunkOptions(options, _props);
        return options;
    }

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
        return new ProgressHarvesterCallback(getChunkHandler(line), _ps);
    }

    public int getChunkSize(CommandLine line)
    {
        String def       = _props.getProperty("defaults.chunkSize");
        String chunkSize = line.getOptionValue("chunkSize", def);
        try                             { return Integer.parseInt(chunkSize); }
        catch (NumberFormatException e) { return DEFAULT_CHUNK_SIZE;          }
    }

    public ChunkHandler getChunkHandler(CommandLine line)
    {
        int  size = getChunkSize(line);
        File file = getOutputFile(line);
        Lang lang = getRDFFormat(line);
        if ( lang == null ) { throw new RuntimeException("Unsupported RDF format"); }

        if ( file.isDirectory() ) { return new DirChunkHandler(file, lang, size); }

        String fn = file.getName();
        if ( fn.endsWith(".zip") ) { return new ZipChunkHandler(file, lang, size); }

        return null;
    }

    public static Options addChunkOptions(Options opts, Properties props)
    {
        opts.addOption(OptionBuilder
                .hasArg()
                .withArgName("chunkSize")
                .withDescription(props.getProperty("info.option.chunkSize"))
                .create("chunkSize"));
        return opts;
    }
}