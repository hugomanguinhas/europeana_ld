package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class GetRecord extends SingleRequestHandler<OAIRecord>
{
    protected static Map<QName,ParserStrategy<HarvesterContext>> _strategies = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strategies);
        new RecordStrategy<HarvesterContext>().initStrategy(_strategies);
        new RecordHeaderStrategy().initStrategy(_strategies);
        new RecordMetadataStrategy<HarvesterContext>().initStrategy(_strategies);
        new RecordAboutStrategy<HarvesterContext>().initStrategy(_strategies);
    }

    public GetRecord(OAIDataSource dataSource, Properties params) {
        super(dataSource, _strategies, params);
    }

    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public String getVerb() { return "GetRecord"; }
}
