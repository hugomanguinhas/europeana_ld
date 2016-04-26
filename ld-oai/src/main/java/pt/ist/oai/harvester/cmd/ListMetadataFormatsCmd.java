package pt.ist.oai.harvester.cmd;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.model.*;

public class ListMetadataFormatsCmd extends VerbCmd
{
    @Override
    protected String getVerb() { return "ListMetadataFormats"; }

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
        options.addOption(OptionBuilder.withArgName("identifier")
                .hasArg()
                .withDescription(getProperty("info.ListMetadataFormats.identifier"))
                .create("identifier"));
        return options;
    }

    @Override
    protected void process(CommandLine line, PrintStream print)
    {
        Properties properties = getProperties(line, "identifier");
        OAIHarvester harvester = new OAIHarvesterImpl(line.getOptionValue("host"));
        List<OAIMetadataFormat> ret = harvester.listMetadataFormats(properties);

        //Print result to output
        ModelPrinter printer = new ModelPrinter();
        int i = 1;
        for(OAIMetadataFormat format : ret)
        {
            print.println("metadata format (" + i + "):");
            printer.print(format, print);
            print.println();
            i++;
        }
    }

    public static void main(String[] args)
    {
        new ListMetadataFormatsCmd().process(args);
    }
}
