package pt.ist.oai.harvester.cmd;

import java.io.*;
import java.util.*;

import javax.xml.transform.stream.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.oai.harvester.model.rw.*;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

public class GetRecordCmd extends VerbCmd
{
    @Override
    protected String getVerb() { return "GetRecord"; }

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
                .isRequired()
                .withDescription(getProperty("info.GetRecord.identifier"))
                .create("identifier"));
        options.addOption(OptionBuilder.withArgName("metadataPrefix")
                .hasArg()
                .isRequired()
                .withDescription(getProperty("info.GetRecord.metadataPrefix"))
                .create("metadataPrefix"));
        options.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription(getProperty("info.GetRecord.file"))
                .create("file"));
        options.addOption(OptionBuilder.withArgName("'record-only'|'both'")
                .hasArg()
                .withDescription(getProperty("info.GetRecord.detail"))
                .create("detail"));
        return options;
    }

    @Override
    protected void process(CommandLine line, PrintStream print)
              throws Throwable {
        Properties props = getProperties(line, "identifier", "metadataPrefix");

        //Get Record
        long elapsed = System.currentTimeMillis();
        OAIHarvester h = new OAIHarvesterImpl(line.getOptionValue("host"));
        OAIRecord record = h.getRecord(props);
        elapsed = System.currentTimeMillis() - elapsed;

        //Print result to output
        ModelPrinter printer = new ModelPrinter();
        printer.print(record, print);
        if(line.hasOption("file"))
        {
            String path = line.getOptionValue("file");
            File out = new File(path).getAbsoluteFile();
            ensureDir(out.getParentFile());
            String detail = line.getOptionValue("detail", "record-only")
                                .toLowerCase();
            if(detail.equals("both")) {
                new OAIWriter().write(record, h.identify()
                                    , new StreamResult(out));
            }
            else if(record.hasMetadata()) { write(record.getMetadata(), out); }
        }
        else {
            //Show record in the screen
            String detail = line.getOptionValue("detail", "record-only");
            if(detail.equals("both")) {
                new OAIWriter().write(record, h.identify()
                                    , new StreamResult(print));
            }
            else {
                if(record.hasMetadata()) {
                    printer.print(record.getMetadata(), print);
                }
            }
        }
        print.println();
        print.println("Successfuly retrieved 1 record in " + elapsed + "ms");
    }

    public static void main(String[] args)
    {
        new GetRecordCmd().process(args);
    }
}
