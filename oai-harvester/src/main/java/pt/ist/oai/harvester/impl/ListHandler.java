package pt.ist.oai.harvester.impl;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class ListHandler<O> extends ResumptionHandler<List<O>,List<O>>
{
    protected Vector<O> _ret;

    public ListHandler(OAIDataSource source
                     , Map<QName,ParserStrategy<HarvesterContext>> strats
                     , Properties params) {
        super(source, strats, params);
    }

    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @SuppressWarnings("unchecked")
    @Override
    public void newObject(Object obj)
    {
        _ret.add((O)obj);
        _info._cursor++;
    }

    @Override
    public void newToken(ResumptionToken token)
    {
        super.newToken(token);
        if(!_token.hasCompleteListSize()) { return; }

        _ret.ensureCapacity((int)token.getCompleteListSize());
    }

    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @SuppressWarnings("deprecation")
    public List<O> handle() throws OAIException
    {
        _info = new OAICmdInfoImpl();
        _token = null;
        _ret = new Vector<O>();
        String baseReq = _source.getBaseURL() + "?verb=" + getVerb();
        String url = getRequestURI();
        while(true)
        {
            try {
                InputStream in = RequestHandler.handle(url);
                parse(new InputSource(in));
                if(_token == null || _token.isComplete()) { break; }

                String value = URLEncoder.encode(_token.getValue());
                url = baseReq + "&resumptionToken=" + value;
            }
            catch(ParsingException p) { throw (OAIException)p.getCause(); }
            catch(IOException e)      { throw new OAIOtherException(e); }
        }

        if(_info._completeListSize < 0) {
            _info._completeListSize = _info._cursor;
        }
        _info._cursor = -1;
        _info._expirationDate = null;
        return _ret;
    }
}
