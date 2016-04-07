package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class ListRecords extends ListHandler<OAIRecord>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strats);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strats);
        new RecordHeaderStrategy().initStrategy(_strats);
        new RecordMetadataStrategy<HarvesterContext>().initStrategy(_strats);
        new RecordAboutStrategy<HarvesterContext>().initStrategy(_strats);
        new RecordStrategy<HarvesterContext>().initStrategy(_strats);
    }

    public ListRecords(OAIDataSource source, Properties params)
    {
        super(source, _strats, params);
    }

    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public String getVerb() { return "ListRecords"; }

    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        OAIRequest<List<OAIRecord>> ret = harvester.newListRecords("gallica:theme:1:19", "oai_dc");
        List<OAIRecord> list = ret.handle();
        System.err.println("ret[" + ret.getInfo().getCompleteListSize() + "," + list.size() + "]");
    }
}
