package pt.ist.oai.harvester.impl;

import java.util.*;

import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.model.*;

public class CountRecords extends CountHandler
{
    public CountRecords(OAIDataSource source, Properties params
                      , HttpClientBuilder builder)
    {
        super(source, params, builder);
    }


    /***************************************************************************
     * Interface HarvesterContext
     **************************************************************************/
    @Override
    public String getVerb() { return "ListRecords"; }
}