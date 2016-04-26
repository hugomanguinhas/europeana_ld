package pt.ist.oai.harvester.impl;

import static pt.ist.oai.harvester.impl.HarvesterUtils.*;

import java.io.IOException;
import java.text.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public abstract class VerbHandler<O,Return>
    extends DefaultParser<O,HarvesterContext>
    implements OAIRequest<Return>, HarvesterContext, ResponseHandler<O>
{
    protected static String DEFAULT_USER_AGENT = "OAIHarvester/2.0";

    protected OAIDataSource       _source;
    protected Properties          _params;
    protected String              _request;
    protected OAICmdInfoImpl      _info;
    protected HttpClientBuilder   _builder = null;


    /***************************************************************************
     * Constructors
     **************************************************************************/

    public VerbHandler(
           OAIDataSource source
         , Map<QName,ParserStrategy<HarvesterContext>> strats
         , Properties params
         , HttpClientBuilder builder)
    {
        super(null, strats);
        _source  = source;
        _context = this;
        _params  = params;
        _request = append(_source.getBaseURL() + "?verb=" + getVerb(), params);
        _builder = builder;
    }


    /***************************************************************************
     * Interface OAIRequest
     **************************************************************************/
    @Override
    public OAIDataSource   getSource()     { return _source;         }
    @Override
    public abstract String getVerb();
    @Override
    public Properties      getParameters() { return _params;         }
    @Override
    public String          getRequestURI() { return _request;        }
    @Override
    public boolean         hasInfo()       { return (_info != null); }
    @Override
    public OAICmdInfo      getInfo()       { return _info;           }


    /***************************************************************************
     * Interface ResponseHandler
     **************************************************************************/

    public O handleResponse(HttpResponse rsp)
           throws ClientProtocolException, IOException
    {
         try { return parse(new InputSource(rsp.getEntity().getContent())); }
         catch(ParsingException p)
         {
             Throwable t = p.getCause();
             if(t instanceof OAIException) throw (OAIException)t;
             throw new OAIOtherException(p);
         }
    }


    /***************************************************************************
     * Interface HarvesterContext
     **************************************************************************/

    @Override
    public Date getNormalizedDate(String datestamp)
    {
        if(datestamp == null) { return null; }
        try { return _source.getGranularity().parseDate(datestamp); }
        catch(ParseException e) { throw new OAIOtherException(e); }
    }

    @Override
    public void newToken(ResumptionToken token)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void newObject(Object obj)
    {
        throw new UnsupportedOperationException();
    }


    /***************************************************************************
     * Protected Methods
     **************************************************************************/
    @Override
    protected void initParser(XMLReader xr) throws SAXException
    {
        super.initParser(xr);
        xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    }
}
