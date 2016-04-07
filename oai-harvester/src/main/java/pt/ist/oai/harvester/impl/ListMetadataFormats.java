package pt.ist.oai.harvester.impl;

import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.impl.strategy.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;
import static pt.ist.oai.harvester.impl.HarvesterUtils.*;

public class ListMetadataFormats extends SingleRequestHandler<List<OAIMetadataFormat>> {

    protected static Map<QName,ParserStrategy<HarvesterContext>> _strats = 
        new HashMap<QName,ParserStrategy<HarvesterContext>>();

    static
    {
        new ErrorStrategy<HarvesterContext>().initStrategy(_strats);

        _strats.put(
            new QName(NAMESPACE_URI, "ListMetadataFormats"), 
            new AbsParserStrategy<HarvesterContext>()
            {

                @Override
                public Object parse(ParserSupport support, HarvesterContext ctx)
                       throws SAXException
                {
                    ParsedObject obj = support.getParsedObject();
                    return obj.getChildren(OAIMetadataFormat.class);
                }
        });
        
        _strats.put(
            new QName(NAMESPACE_URI, "metadataFormat"), 
            new AbsParserStrategy<HarvesterContext>()
            {
                @Override
                public Object parse(ParserSupport support, HarvesterContext ctx)
                       throws SAXException
                {
                    ParsedObject obj = support.getParsedObject();
                    String metadataPrefix = getContent(obj, "metadataPrefix");
                    String schemaLocation = getContent(obj, "schema");
                    String metadataNS     = getContent(obj, "metadataNamespace");
                    return new OAIMetadataFormat(metadataNS
                                               , metadataPrefix
                                               , schemaLocation);
                }
        });

        DefaultParserStrategy<HarvesterContext> def
            = DefaultParserStrategy.getSingleton();
        _strats.put(new QName(NAMESPACE_URI, "metadataPrefix")   , def);
        _strats.put(new QName(NAMESPACE_URI, "schema")           , def);
        _strats.put(new QName(NAMESPACE_URI, "metadataNamespace"), def);
    }

    protected String _request;

    public ListMetadataFormats(OAIDataSource dataSource, Properties params)
    {
        super(dataSource, _strats, params);
        _request = append(_source.getBaseURL() + "?verb=" + getVerb(), params);
    }

    /****************************************************/
    /*                Interface OAIRequest               */
    /****************************************************/
    @Override
    public String getVerb() { return "ListMetadataFormats"; }

    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://bd2.inesc-id.pt:8080/repox2Eudml/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        List<OAIMetadataFormat> ret = harvester.listMetadataFormats();
        System.err.println("ret=" + ret);
    }
}
