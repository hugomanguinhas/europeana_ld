package pt.ist.oai.harvester.impl;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.*;
import pt.ist.util.sync.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class IterationHandler<O> 
    extends ResumptionHandler<O,CloseableIterable<O>> 
    implements Runnable, CloseableIterable<O>, Iterator<O>
{
    protected StoppableMutex _consumer = new StoppableMutex();
    protected StoppableMutex _producer = new StoppableMutex();
    protected Object         _return;

    public IterationHandler(OAIDataSource source
                          , Map<QName,ParserStrategy<HarvesterContext>> strats
                          , Properties params, HttpClientBuilder builder)
    {
        super(source, strats, params, builder);
    }


    /***************************************************************************
     * Interface OAIRequest
     **************************************************************************/
    public CloseableIterable<O> handle() throws OAIException
    {
        _info   = new OAICmdInfoImpl();
        _token  = null;
        _return = null;
        _producer.start();
        _consumer.start();
        new Thread(this).start();
        return this;
    }


    /***************************************************************************
     * Interface HarvesterContext
     **************************************************************************/
    @Override
    public void newObject(Object obj)
    {
        _producer.acquire();
        _return = obj;
        _consumer.release();
        _info._cursor++;
    }


    /***************************************************************************
     * Interface Runnable
     **************************************************************************/
    @SuppressWarnings("deprecation")
    @Override
    public void run()
    {
        CloseableHttpClient client = null;
        try {
            client = _builder.build();
            _token = null;
            String url = getRequestURI();
            String baseRequest = _source.getBaseURL() + "?verb=" + getVerb();
            while(true)
            {
                client.execute(new HttpGet(url), this);

                if(_token == null || _token.isComplete()) { break; }
                url = baseRequest + "&resumptionToken="
                    + URLEncoder.encode(_token.getValue());
            }
        }
        catch(ParsingException p) {
            _return = (OAIException)p.getCause(); return;
        }
        catch(IOException e) {
            _return = new OAIOtherException(e); return;
        }
        catch(StoppingException e) {}
        finally { close(); IOUtils.closeQuietly(client); }

        if(_info._completeListSize < 0) {
            _info._completeListSize = _info._cursor;
        }
        _info._cursor = -1;
        _info._expirationDate = null;
    }


    /***************************************************************************
     * Interface Iterable
     **************************************************************************/
    @Override
    public Iterator<O> iterator() { return this; }


    /***************************************************************************
     * Interface Iterator
     **************************************************************************/
    @Override
    public boolean hasNext()
    {
        try {
            try {
                while(_return == null)
                {
                    _producer.release();
                    _consumer.acquire();
                }
                return true;
            }
            catch(StoppingException e) {
                return false;
            }
        }
        finally {
            if(_return instanceof OAIException) {
                close();
                throw (OAIException)_return;
            }
        }
    }

    @Override
    public O next()
    {
        try {
            while(_return == null)
            {
                _producer.release();
                _consumer.acquire();
            }
        }
        finally {
            if(_return instanceof OAIException) {
                close();
                throw (OAIException)_return;
            }
        }
        @SuppressWarnings("unchecked")
        O ret = (O)_return;
        _return = null;
        return ret;
    }

    @Override
    public void remove() { throw new UnsupportedOperationException(); }


    /***************************************************************************
     * Interface CloseableIterable
     **************************************************************************/
    public void close()
    {
        _producer.stop();
        _consumer.stop();
    }
}