package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.*;
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

    public ListSets(OAIDataSource source, Properties params)
    {
        super(source, _strats, params);
    }


    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public String getVerb() { return "ListSets"; }

    public static final void main(String[] args) throws Exception
    {
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        OAIRequest<List<OAIMetadataSet>> ret = harvester.newListSets();
        List<OAIMetadataSet> list = ret.handle();
        System.err.println("ret[" + ret.getInfo().getCompleteListSize() + "]["
                         + list.size() + "]=" + ret);
    }
}
