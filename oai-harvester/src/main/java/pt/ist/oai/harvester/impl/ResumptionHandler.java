package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class ResumptionHandler<O,Return> extends VerbHandler<O,Return>
{
    protected ResumptionToken _token;
    protected OAICmdInfoImpl  _info;

    public ResumptionHandler(OAIDataSource source
                           , Map<QName,ParserStrategy<HarvesterContext>> strats
                           , Properties params)
    {
        super(source, strats, params);
    }


    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public boolean hasInfo() { return (_info != null); }

    @Override
    public OAICmdInfo getInfo() { return _info; }


    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @Override
    public void newToken(ResumptionToken token)
    {
        _token = token;
        if(_token.hasCompleteListSize()) {
            _info._completeListSize = token.getCompleteListSize();
        }
        Date date = token.getExpirationDate();
        if(date != null) { _info._expirationDate = date; }
    }
}
