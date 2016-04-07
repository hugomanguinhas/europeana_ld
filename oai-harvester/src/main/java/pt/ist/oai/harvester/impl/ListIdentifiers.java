package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class ListIdentifiers extends ListHandler<OAIRecordHeader>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strats);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strats);
        new RecordHeaderStrategy(true).initStrategy(_strats);
    }

    public ListIdentifiers(OAIDataSource source, Properties params)
    {
        super(source, _strats, params);
    }

    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public String getVerb() { return "ListIdentifiers"; }

    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        List<OAIRecordHeader> ret = harvester.listIdentifiers("gallica:theme:8:80", "oai_dc");
        System.err.println("ret[" + ret.size() + "]");
    }
}
