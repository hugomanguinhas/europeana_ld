package pt.ist.oai.harvester.impl;

import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class IterateIdentifiers extends IterationHandler<OAIRecordHeader>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strategies = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strategies);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strategies);
        new RecordHeaderStrategy()
        {
            @Override
            public Object parse(ParserSupport support, HarvesterContext ctx) 
                   throws SAXException
            {
                Object obj = super.parse(support, ctx); ctx.newObject(obj);
                return obj;
            }
        }.initStrategy(_strategies);
    }

    public IterateIdentifiers(OAIDataSource source, Properties params)
    {
        super(source, _strategies, params);
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
        int i = 0;
        CloseableIterable<OAIRecordHeader> iter
            = harvester.iterateIdentifiers("gallica:theme:1:19", "oai_dc");
        try {
            for(OAIRecordHeader header : iter)
            {
                if(new Random().nextInt(1000) < 1) { break; }
                System.err.println("header[" + (++i) + "]=" + header.getIdentifier());
            }
        }
        finally { iter.close(); }
    }
}