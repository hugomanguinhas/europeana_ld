/**
 * 
 */
package eu.europeana.ld.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Oct 2016
 */
public abstract class ToolkitCmd
{
    private static Logger _log = Logger.getLogger(ToolkitCmd.class);
    protected static final Properties PROPERTIES = new Properties();
    
    protected static void loadProperties(String cfg)
    {
        try {
            InputStream is = ToolkitCmd.class.getResourceAsStream(cfg);
            PROPERTIES.loadFromXML(is);
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    protected static String getProperty(String key)
    {
        return PROPERTIES.getProperty(key);
    }

    protected HelpFormatter _formatter = new HelpFormatter();
    protected PrintStream   _ps        = System.out;

    public ToolkitCmd() {}

    public void process(String[] args)
    {
        //Build Options
        Options opts = buildOptions();

        //Create parser
        CommandLineParser parser = new GnuParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(opts, args);
            if(line.hasOption("help")) { printUsage(opts); return; }

            printHeader();
            try                { process(line); }
            catch(Throwable t) { printError(t); }
            printFooter();
        }
        catch(ParseException exp)
        {
            //oops, something went wrong
            printUsage(opts, exp.getMessage());
        }
    }

    protected abstract Options buildOptions();

    protected abstract void process(CommandLine line) throws Throwable;

    protected void printUsage(Options opts)
    {
        String name = getProperty("info.name");
        _formatter.printHelp(name, opts, true);
    }

    protected void printUsage(Options opts, String msg)
    {
        printHeader();
        _ps.println(msg);
        _ps.println();
        printUsage(opts);
    }

    protected void printHeader()
    {
        _ps.print(PROPERTIES.getProperty("layout.header"));
    }

    protected void printFooter()
    {
        _ps.print(PROPERTIES.getProperty("layout.footer"));
    }

    protected void printError(Throwable t)
    {
        _log.error("Unexpected error", t);
        _ps.println("Error: " + t.getMessage());
        _ps.println();
        t.printStackTrace(_ps);
    }
}