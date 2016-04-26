package pt.ist.oai.harvester.impl;

import java.util.*;

import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class ListSets extends ListHandler<OAIMetadataSet>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strats);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strats);
        new SetStrategy().initStrategy(_strats);
    }


    /***************************************************************************
     * Constructors
     **************************************************************************/
    public ListSets(OAIDataSource source, Properties params
                  , HttpClientBuilder builder)
    {
        super(source, _strats, params, builder);
    }


    /***************************************************************************
     * Interface OAIRequest
     **************************************************************************/
    @Override
    public String getVerb() { return "ListSets"; }
}
