package pt.ist.oai.harvester.impl;

import java.util.*;

import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.*;

import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

public class IterateIdentifiers extends IterationHandler<OAIRecordHeader>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strats);
        new ResumptionTokenStrategy<HarvesterContext>().initStrategy(_strats);
        new RecordHeaderStrategy()
        {
            @Override
            public Object parse(ParserSupport support, HarvesterContext ctx) 
                   throws SAXException
            {
                Object obj = super.parse(support, ctx); ctx.newObject(obj);
                return obj;
            }
        }.initStrategy(_strats);
        new ResponseDateStrategy<HarvesterContext>().initStrategy(_strats);
    }


    /***************************************************************************
     * Constructors
     **************************************************************************/

    public IterateIdentifiers(OAIDataSource source, Properties params
                            , HttpClientBuilder builder)
    {
        super(source, _strats, params, builder);
    }


    /***************************************************************************
     * Interface OAIRequest
     **************************************************************************/
    @Override
    public String getVerb() { return "ListIdentifiers"; }
}