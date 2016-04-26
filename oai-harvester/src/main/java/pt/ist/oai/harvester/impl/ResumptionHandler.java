package pt.ist.oai.harvester.impl;

import java.util.*;

import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public abstract class ResumptionHandler<O,Return> extends VerbHandler<O,Return>
{
    protected ResumptionToken _token;


    /***************************************************************************
     * Constructors
     **************************************************************************/

    public ResumptionHandler(OAIDataSource source
                           , Map<QName,ParserStrategy<HarvesterContext>> strats
                           , Properties params, HttpClientBuilder builder)
    {
        super(source, strats, params, builder);
    }


    /***************************************************************************
     * Interface HarvesterContext
     **************************************************************************/
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
