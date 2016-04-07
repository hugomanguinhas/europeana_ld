package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class IterateSets extends IterationHandler<OAIMetadataSet>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strats);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strats);
        new SetStrategy().initStrategy(_strats);
    }

    public IterateSets(OAIDataSource source, Properties params)
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
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        int i = 0;
        OAIRequest<CloseableIterable<OAIMetadataSet>> req = harvester.newIterateSets();
        CloseableIterable<OAIMetadataSet> iter = req.handle();
        try {
            for(OAIMetadataSet set : iter) {
                //if(new Random().nextInt(1000) < 1) break;
                System.err.println("set[" + (++i) + "][" + req.getInfo().getCompleteListSize() + "]=" + set);
            }
        }
        finally {
            iter.close();
        }
    }
}