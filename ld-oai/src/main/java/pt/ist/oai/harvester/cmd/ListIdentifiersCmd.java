package pt.ist.oai.harvester.cmd;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

import java.io.PrintStream;
import java.util.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.CloseableIterable;

public class ListIdentifiersCmd extends VerbCmd
{
    @Override
    protected String getVerb() { return "ListIdentifiers"; }

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
        options.addOption(OptionBuilder.withArgName("from")
                .hasArg()
                .withDescription(getProperty("info.ListIdentifiers.from"))
                .create("from"));
        options.addOption(OptionBuilder.withArgName("until")
                .hasArg()
                .withDescription(getProperty("info.ListIdentifiers.until"))
                .create("until"));
        options.addOption(OptionBuilder.withArgName("metadataPrefix")
                .hasArg()
                .isRequired()
                .withDescription(getProperty("info.ListIdentifiers.metadataPrefix"))
                .create("metadataPrefix"));
        options.addOption(OptionBuilder.withArgName("set")
                .hasArg()
                .withDescription(getProperty("info.ListIdentifiers.set"))
                .create("set"));
        return options;
    }

    @Override
    protected void process(CommandLine line, PrintStream ps)
    {
        Properties   props = getProperties(line, "from", "until"
                                         , "metadataPrefix", "set");
        OAIHarvester h     = new OAIHarvesterImpl(line.getOptionValue("host"));
        CloseableIterable<OAIRecordHeader> ret = h.iterateIdentifiers(props);
        //Print result to output
        ModelPrinter printer = new ModelPrinter(h.identify());
        int i = 1;
        try {
            for(OAIRecordHeader header : ret) {
                ps.println("record (" + i + "):");
                printer.print(header, ps);
                ps.println();
                i++;
            }
        }
        finally { ret.close(); }
    }

    public static void main(String[] args)
    {
        new ListIdentifiersCmd().process(args);
    }
}
