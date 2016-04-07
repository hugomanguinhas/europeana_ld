package pt.ist.oai.harvester.impl;

import java.io.*;
import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.impl.strategy.ErrorStrategy;
import pt.ist.oai.harvester.model.*;
import pt.ist.oai.harvester.model.OAIDataSource.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;
import static pt.ist.oai.harvester.impl.HarvesterUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class Identify extends DefaultParser<OAIDataSource,ParserContext>
                      implements ParserContext
{
    protected static Map<QName,ParserStrategy<ParserContext>> _strategies = 
        new HashMap<QName,ParserStrategy<ParserContext>>();

    static
    {
        new ErrorStrategy<ParserContext>().initStrategy(_strategies);

        _strategies.put(
            new QName(NAMESPACE_URI, "Identify"), 
            new AbsParserStrategy<ParserContext>()
            {
                @Override
                public Object parse(ParserSupport support, ParserContext context) throws SAXException {
                    ParsedObject obj = support.getParsedObject();
                    String repositoryName = getContent(obj, "repositoryName");
                    String baseURL = getContent(obj, "baseURL");
                    String protocolVersion = getContent(obj, "protocolVersion");
                    List<String> adminEmails = getContentAsList(obj, "adminEmail");
                    GranularityType granularity = GranularityType.parse(getContent(obj, "granularity"));
                    Date earliestDatestamp = getNormalizedDate(getContent(obj, "earliestDatestamp"), granularity);
                    DeletedRecordType deletedRecord = parseEnum(DeletedRecordType.class, getContent(obj, "deletedRecord"));
                    List<String> compression = getContentAsList(obj, "compression");
                    return new OAIDataSource(
                            repositoryName, baseURL, protocolVersion, adminEmails, earliestDatestamp, 
                            deletedRecord, granularity, compression);
                }
            }
        );

        DefaultParserStrategy<ParserContext> def = DefaultParserStrategy.getSingleton(ParserContext.class);
        _strategies.put(new QName(NAMESPACE_URI, "repositoryName"), def);
        _strategies.put(new QName(NAMESPACE_URI, "baseURL"), def);
        _strategies.put(new QName(NAMESPACE_URI, "protocolVersion"), def);
        _strategies.put(new QName(NAMESPACE_URI, "adminEmail"), def);
        _strategies.put(new QName(NAMESPACE_URI, "earliestDatestamp"), def);
        _strategies.put(new QName(NAMESPACE_URI, "deletedRecord"), def);
        _strategies.put(new QName(NAMESPACE_URI, "granularity"), def);
        _strategies.put(new QName(NAMESPACE_URI, "compression"), def);
    }

    public Identify() { super(null, _strategies); }

    @Override
    protected void initParser(XMLReader xr) throws SAXException
    {
        super.initParser(xr);
        xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    }

    public OAIDataSource identify(String baseURL) throws OAIException
    {
        try {
            InputStream in = RequestHandler.handle(baseURL + "?verb=Identify");
            try {
                return parse(new InputSource(in));
            }
            catch(ParsingException p) {
                Throwable t = p.getCause();
                if(t instanceof OAIException) throw (OAIException)t;
                throw new OAIOtherException(p);
            }
        }
        catch(IOException e) {
            throw new OAIOtherException(e);
        }
    }

    public static final void main(String[] args) throws Exception
    {
        OAIDataSource source = 
            new Identify().identify("http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler?verb=Identify");
        System.err.println("source=" + source);
    }
}
