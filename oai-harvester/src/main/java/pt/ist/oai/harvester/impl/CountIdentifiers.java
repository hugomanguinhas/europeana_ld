package pt.ist.oai.harvester.impl;

import java.util.Properties;

import pt.ist.oai.harvester.model.*;

public class CountIdentifiers extends CountHandler
{
    public CountIdentifiers(OAIDataSource source, Properties params)
    {
        super(source, params);
    }

    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @Override
    public String getVerb() { return "ListIdentifiers"; }
}