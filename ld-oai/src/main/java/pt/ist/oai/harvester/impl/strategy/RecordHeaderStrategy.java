package pt.ist.oai.harvester.impl.strategy;

import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.impl.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;
import static pt.ist.oai.harvester.impl.HarvesterUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class RecordHeaderStrategy extends AbsParserStrategy<HarvesterContext>
{
    protected boolean _register;

    public RecordHeaderStrategy(boolean register) { _register = register; }
    public RecordHeaderStrategy()                 { this(false); }

    public void initStrategy(Map<QName,ParserStrategy<HarvesterContext>> strats)
    {
        DefaultParserStrategy<HarvesterContext> def
            = DefaultParserStrategy.getSingleton(HarvesterContext.class);
        strats.put(new QName(NAMESPACE_URI, "header"    ), this);
        strats.put(new QName(NAMESPACE_URI, "identifier"), def);
        strats.put(new QName(NAMESPACE_URI, "datestamp" ), def);
        strats.put(new QName(NAMESPACE_URI, "setSpec"   ), def);
    }

    @Override
    public Object parse(ParserSupport support, HarvesterContext context)
           throws SAXException
    {
        ParsedObject obj = support.getParsedObject();
        boolean deleted = isDeleted(obj.getAttributes().getAttributeNode("status"));
        String identifier = getContent(obj, "identifier");
        Date datestamp = context.getNormalizedDate(getContent(obj, "datestamp"));
        Collection<String> setSpec = getContentAsList(obj, "setSpec");
        OAIRecordHeader header = new OAIRecordHeader(identifier, datestamp, setSpec, deleted);
        if(_register) { context.newObject(header); }
        return header;
    }

    protected boolean isDeleted(pt.ist.xml.Attribute attr)
    {
        return (attr != null && attr.getValue().equals("deleted"));
    }
}