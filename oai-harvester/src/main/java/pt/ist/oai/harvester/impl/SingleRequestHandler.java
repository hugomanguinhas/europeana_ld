package pt.ist.oai.harvester.impl;

import java.io.*;
import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class SingleRequestHandler<O> extends VerbHandler<O,O>
{
    public SingleRequestHandler(
            OAIDataSource source, Map<QName,ParserStrategy<HarvesterContext>> strategies, 
            Properties params) {
        super(source, strategies, params);
    }


    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public O handle() throws OAIException
    {
        try {
            InputStream in = RequestHandler.handle(getRequestURI());
            return parse(new InputSource(in));
        }
        catch(ParsingException p) {
            Throwable t = p.getCause();
            if(t instanceof OAIException) { throw (OAIException)t; }
            throw new OAIOtherException(p);
        }
        catch(IOException e) { throw new OAIOtherException(e); }
    }

    @Override
    public boolean hasInfo() { return false; }

    @Override
    public OAICmdInfoImpl getInfo() { return null; }


    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @Override
    public void newObject(Object obj) {}
}