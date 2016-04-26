package pt.ist.oai.harvester.cmd;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.apache.commons.cli.*;
import org.w3c.dom.Document;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

public abstract class VerbCmd
{
    protected Properties getProperties(CommandLine line, String... args)
    {
        Properties props = new Properties();
        for(String arg : args)
        {
            if(!line.hasOption(arg)) { continue; }
            props.put(arg, line.getOptionValue(arg));
        }
        return props;
    }

    protected void ensureDir(File file)
    {
        if(!file.exists()) { file.mkdirs(); }
    }

    protected void write(Document doc, File out)
    {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(out));
        }
        catch (TransformerException | TransformerFactoryConfigurationError e) {}
    }

    protected abstract Options buildOptions();

    protected abstract String getVerb();

    protected abstract void process(CommandLine line, PrintStream print)
              throws Throwable;

    public void process(String[] args)
    {
        //Build Options
        Options options = buildOptions();

        //Create parser
        CommandLineParser parser = new GnuParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if(line.hasOption("help")) {
                printVerbUsage(System.out, getVerb(), options);
                return;
            }

            printVerbHeader(System.out, getVerb());
            printProtocolResponse(System.out);
            try { process(line, System.out); }
            catch(Throwable t) { printProtocolError(t, System.out); }
        }
        catch(ParseException exp)
        {
            //oops, something went wrong
            printVerbUsage(System.out, getVerb(), options, exp.getMessage());
        }
    }
}
