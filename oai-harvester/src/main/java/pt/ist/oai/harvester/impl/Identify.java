package pt.ist.oai.harvester.impl;

import java.io.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.impl.strategy.ErrorStrategy;
import pt.ist.oai.harvester.model.*;
import pt.ist.oai.harvester.model.OAIDataSource.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;
import static pt.ist.oai.harvester.OAIConstants.*;
import static pt.ist.oai.harvester.impl.HarvesterUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class Identify extends DefaultParser<OAIDataSource,ParserContext>
                      implements ParserContext, ResponseHandler<OAIDataSource>
{
    protected static Map<QName,ParserStrategy<ParserContext>> _strats = 
        new HashMap<QName,ParserStrategy<ParserContext>>();

    static
    {
        new ErrorStrategy<ParserContext>().initStrategy(_strats);

        _strats.put(
            new QName(NAMESPACE_URI, "Identify"), 
            new AbsParserStrategy<ParserContext>()
            {
                @Override
                public Object parse(ParserSupport support
                                  , ParserContext context) throws SAXException {
                    ParsedObject obj = support.getParsedObject();
                    String repositoryName = getContent(obj, "repositoryName");
                    String baseURL = getContent(obj, "baseURL");
                    String protocolVersion = getContent(obj, "protocolVersion");
                    List<String> adminEmails = getContentAsList(obj, "adminEmail");
                    GranularityType granularity = GranularityType.parse(getContent(obj, "granularity"));
                    Date earliestDatestamp = getNormalizedDate(getContent(obj, "earliestDatestamp"), granularity);
                    DeletedRecordType deletedRecord = parseEnum(DeletedRecordType.class, getContent(obj, "deletedRecord"));
                    List<String> compression = getContentAsList(obj, "compression");
                    return new OAIDataSource(
                            repositoryName, baseURL, protocolVersion, adminEmails, earliestDatestamp, 
                            deletedRecord, granularity, compression);
                }
            }
        );

        DefaultParserStrategy<ParserContext> def
            = DefaultParserStrategy.getSingleton(ParserContext.class);
        _strats.put(new QName(NAMESPACE_URI, "repositoryName")   , def);
        _strats.put(new QName(NAMESPACE_URI, "baseURL")          , def);
        _strats.put(new QName(NAMESPACE_URI, "protocolVersion")  , def);
        _strats.put(new QName(NAMESPACE_URI, "adminEmail")       , def);
        _strats.put(new QName(NAMESPACE_URI, "earliestDatestamp"), def);
        _strats.put(new QName(NAMESPACE_URI, "deletedRecord")    , def);
        _strats.put(new QName(NAMESPACE_URI, "granularity")      , def);
        _strats.put(new QName(NAMESPACE_URI, "compression")      , def);
    }

    protected HttpClientBuilder _builder = null;

    /***************************************************************************
     * Constructors
     **************************************************************************/

    public Identify(HttpClientBuilder builder)
    {
        super(null, _strats);
        _builder = builder;
    }


    /***************************************************************************
     * Public Methods
     **************************************************************************/
    public OAIDataSource identify(String baseURL) throws OAIException
    {
        CloseableHttpClient client = null;
        try {
            client = _builder.build();
            return client.execute(new HttpGet(baseURL + "?verb=Identify"), this);
        }
        catch(IOException e) {
            throw new OAIOtherException(e);
        }
        finally { IOUtils.closeQuietly(client); }
    }

    /***************************************************************************
     * Interface ResponseHandler
     **************************************************************************/

    public OAIDataSource handleResponse(HttpResponse rsp)
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
     * Protected Methods
     **************************************************************************/
    @Override
    protected void initParser(XMLReader xr) throws SAXException
    {
        super.initParser(xr);
        xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    }
}
