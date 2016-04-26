package pt.ist.oai.harvester.impl;

import java.util.Properties;

import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.model.*;

public class CountIdentifiers extends CountHandler
{
    public CountIdentifiers(OAIDataSource source, Properties params
                          , HttpClientBuilder builder)
    {
        super(source, params, builder);
    }

    /***************************************************************************
     * Interface HarvesterContext
     **************************************************************************/
    @Override
    public String getVerb() { return "ListIdentifiers"; }
}