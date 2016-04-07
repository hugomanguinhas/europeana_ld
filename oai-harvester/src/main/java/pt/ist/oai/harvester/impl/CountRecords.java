package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.model.*;

public class CountRecords extends CountHandler
{
    public CountRecords(OAIDataSource source, Properties params)
    {
        super(source, params);
    }

    /****************************************************/
    /*             Interface HarvesterContext           */
    /****************************************************/
    @Override
    public String getVerb() { return "ListRecords"; }
}