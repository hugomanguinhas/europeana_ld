package pt.ist.oai.harvester.cmd;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.getProperty;

import java.io.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.*;

public class IdentifyCmd extends VerbCmd
{
    @Override
    protected String getVerb() { return "Identify"; }

    @Override
    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options opts = new Options();
        opts.addOption(new Option("help", getProperty("info.Verbs.help")));
        opts.addOption(OptionBuilder.withArgName("host")
            .hasArg()
            .withDescription(getProperty("info.Verbs.host"))
            .isRequired()
            .create("host"));
        return opts;
    }

    @Override
    protected void process(CommandLine line, PrintStream ps)
    {
        OAIHarvester h = new OAIHarvesterImpl(line.getOptionValue("host"));
        new ModelPrinter().print(h.identify(), ps);
    }

    public static void main(String[] args)
    {
        new IdentifyCmd().process(args);
    }
}
