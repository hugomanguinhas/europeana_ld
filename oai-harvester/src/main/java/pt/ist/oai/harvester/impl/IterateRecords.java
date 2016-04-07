package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class IterateRecords extends IterationHandler<OAIRecord>
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

    public IterateRecords(OAIDataSource source, Properties params)
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
        int i = 0;
        OAIRequest<CloseableIterable<OAIRecord>> req
            = harvester.newIterateRecords("gallica:theme:1:19", "oai_dc");
        CloseableIterable<OAIRecord> iter = req.handle();
        try {
            for(OAIRecord record : iter)
            {
                //if(new Random().nextInt(1000) < 1) break;
                System.err.println("record[" + (++i) + "]["
                                 + req.getInfo().getCompleteListSize() + "]=" 
                                 + record);
            }
        }
        finally { iter.close(); }
    }
}