package pt.ist.oai.harvester.impl;

import static pt.ist.oai.harvester.OAIConstants.*;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class CountHandler extends ResumptionHandler<Long,Long>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strategies = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strategies);
        new ResumptionTokenStrategy<HarvesterContext>()
            .initStrategy(_strategies);

        DefaultParserStrategy<HarvesterContext> def
          = DefaultParserStrategy.getSingleton(HarvesterContext.class);

        _strategies.put(new QName(NAMESPACE_URI, "header")
                      , new AbsParserStrategy<HarvesterContext>() {
            @Override
            public Object parse(ParserSupport parser, HarvesterContext ctxt)
                          throws SAXException
            {
                ctxt.newObject(null);
                return null;
            }
        });
        _strategies.put(new QName(NAMESPACE_URI, "identifier"), def);
        _strategies.put(new QName(NAMESPACE_URI, "datestamp"), def);
        _strategies.put(new QName(NAMESPACE_URI, "setSpec"), def);
        _strategies.put(new QName(NAMESPACE_URI, "ListIdentifiers"), def);
        _strategies.put(new QName(NAMESPACE_URI, "ListRecords"), def);
    }

    public CountHandler(OAIDataSource source, Properties params)
    {
        super(source, _strategies, params);
    }

    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @Override
    public void newObject(Object obj) { _info._cursor++; }

    /****************************************************/
    /*                Interface OAIRequest              */
    /****************************************************/
    @SuppressWarnings("deprecation")
    public Long handle() throws OAIException
    {
        _info = new OAICmdInfoImpl();
        _token = null;
        String baseRequest = _source.getBaseURL() + "?verb=" + getVerb();
        String url = getRequestURI();
        while(true)
        {
            try {
                InputStream in = RequestHandler.handle(url);
                try {
                    parse(new InputSource(in));
                    if(_token == null || _token.isComplete()) { break; }
                    if(_token.hasCompleteListSize()) {
                        _info._completeListSize = _token.getCompleteListSize();
                        return _info._completeListSize;
                    }
                    url = baseRequest + "&resumptionToken="
                        + URLEncoder.encode(_token.getValue());
                }
                catch(ParsingException p) { throw (OAIException)p.getCause(); }
            }
            catch(IOException e) { throw new OAIOtherException(e); }
        }

        if(_info._completeListSize < 0) { 
            _info._completeListSize = _info._cursor;
        }
        _info._cursor = -1;
        _info._expirationDate = null;
        return _info._completeListSize;
    }
}
