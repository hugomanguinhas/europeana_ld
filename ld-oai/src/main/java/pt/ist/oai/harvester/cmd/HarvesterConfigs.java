package pt.ist.oai.harvester.cmd;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.exceptions.*;

public class HarvesterConfigs
{
    public static final String EXE_NAME = "harvester";

    protected static final Properties PROPERTIES = new Properties();

    static {
        try {
            Class c = HarvesterCmd.class;
            PROPERTIES.loadFromXML(c.getResourceAsStream("config.xml"));
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static String getProperty(String key)
    {
        return PROPERTIES.getProperty(key);
    }

    public static void printMainHeader(PrintStream ps)
    {
        ps.print(PROPERTIES.getProperty("layout.header"));
    }

    public static void printMainUsage(PrintStream ps)
    {
        printMainHeader(ps);
        ps.print(PROPERTIES.getProperty("info.Verbs"));
    }

    public static void printMainUsage(PrintStream ps, String msg)
    {
        printMainHeader(System.out);
        ps.println();
        ps.print("error: " + msg);
        ps.println();
        ps.print(PROPERTIES.getProperty("info.Verbs"));
    }

    public static void printVerbHeader(PrintStream ps, String verb)
    {
        printMainHeader(System.out);
        ps.println();
        String info = PROPERTIES.getProperty("info." + verb);
        ps.print("  " + verb + ":");
        ps.print(info);
    }

    public static void printVerbUsage(PrintStream ps, String verb, Options opt)
    {
        printVerbHeader(ps, verb);
        ps.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(EXE_NAME + " " + verb, opt, true);
    }

    public static void printVerbUsage(PrintStream ps, String verb
                                    , Options opts, String msg)
    {
        printVerbHeader(ps, verb);
        ps.println();
        ps.println(msg);
        ps.println();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(EXE_NAME + " " + verb, opts, true);
    }

    public static void printProtocolResponse(PrintStream ps)
    {
        ps.println(PROPERTIES.getProperty("layout.response"));
    }

    public static void printProtocolError(Throwable t, PrintStream ps)
    {
        ps.println();
        if(!(t instanceof OAIException)) {
            ps.print("Unknown error: ");
            t.printStackTrace(ps);
            return;
        }
        OAIException e = (OAIException)t;
        if(e instanceof OAIOtherException) {
            ps.print("Protocol response error: ");
            t.getCause().printStackTrace(ps);
            return;
        }
        ps.println("Protocol response error: " + e.getErrorCode());
    }
}