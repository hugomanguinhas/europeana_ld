package pt.ist.oai.harvester.impl;

import java.util.*;

import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class IterateRecords extends IterationHandler<OAIRecord>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>()          .initStrategy(_strats);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strats);
        new RecordHeaderStrategy()                     .initStrategy(_strats);
        new RecordMetadataStrategy<HarvesterContext>() .initStrategy(_strats);
        new RecordAboutStrategy<HarvesterContext>()    .initStrategy(_strats);
        new RecordStrategy<HarvesterContext>()         .initStrategy(_strats);
        new ResponseDateStrategy<HarvesterContext>()   .initStrategy(_strats);
    }


    /***************************************************************************
     * Constructors
     **************************************************************************/

    public IterateRecords(OAIDataSource source, Properties params
                        , HttpClientBuilder builder)
    {
        super(source, _strats, params, builder);
    }


    /***************************************************************************
     * Interface OAIRequest
     **************************************************************************/
    @Override
    public String getVerb() { return "ListRecords"; }
}