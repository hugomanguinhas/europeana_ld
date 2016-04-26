package pt.ist.oai.harvester.cmd;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

import java.io.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.CloseableIterable;

public class ListSetsCmd extends VerbCmd
{
    @Override
    protected String getVerb() { return "ListSets"; }

    @Override
    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        options.addOption(new Option("help", getProperty("info.Verbs.help")));
        options.addOption(OptionBuilder.withArgName("host")
                .hasArg()
                .withDescription(getProperty("info.Verbs.host"))
                .isRequired()
                .create("host"));
        return options;
    }

    @Override
    protected void process(CommandLine line, PrintStream ps)
    {
        OAIHarvester harvester = new OAIHarvesterImpl(line.getOptionValue("host"));
        CloseableIterable<OAIMetadataSet> ret = harvester.iterateSets();
        //Print result to output
        ModelPrinter printer = new ModelPrinter(harvester.identify());
        int i = 1;
        try {
            for(OAIMetadataSet set : ret)
            {
                ps.println("metadata set (" + i + "):");
                printer.print(set, ps);
                ps.println();
                i++;
            }
        }
        finally { ret.close(); }
    }

    public static void main(String[] args)
    {
        new ListSetsCmd().process(args);
    }
}
