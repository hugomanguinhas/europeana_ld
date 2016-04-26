package pt.ist.oai.harvester.impl;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class SingleRequestHandler<O> extends VerbHandler<O,O>
{
    public SingleRequestHandler(
            OAIDataSource source
          , Map<QName,ParserStrategy<HarvesterContext>> strategies
          , Properties params, HttpClientBuilder builder) {
        super(source, strategies, params, builder);
    }


    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public O handle() throws OAIException
    {
        _info = new OAICmdInfoImpl();
        CloseableHttpClient client = null;
        try {
            client = _builder.build();
            return client.execute(new HttpGet(getRequestURI()), this);
        }
        catch(ParsingException p) {
            Throwable t = p.getCause();
            if(t instanceof OAIException) { throw (OAIException)t; }
            throw new OAIOtherException(p);
        }
        catch(IOException e) { throw new OAIOtherException(e); }
        finally { IOUtils.closeQuietly(client); }
    }


    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @Override
    public void newObject(Object obj) {}
}