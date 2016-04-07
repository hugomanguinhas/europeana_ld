package pt.ist.oai.harvester.impl.strategy;

import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import pt.ist.oai.harvester.impl.HarvesterContext;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class RecordStrategy<Context extends HarvesterContext>
    extends AbsParserStrategy<Context>
{
    public void initStrategy(Map<QName,ParserStrategy<Context>> strats)
    {
        strats.put(new QName(NAMESPACE_URI, "record"), this);
    }

    @Override
    public Object parse(ParserSupport support, Context context)
           throws SAXException
    {
        ParsedObject    obj    = support.getParsedObject();
        OAIRecordHeader header = obj.getChild(OAIRecordHeader.class);

        ParsedObject metaObj = obj.getChildNS(NAMESPACE_URI, "metadata");
        Document metadata = null;
        if(metaObj != null) { metadata = metaObj.getChild(Document.class); }

        Document about = null;
        ParsedObject aboutObj = obj.getChildNS(NAMESPACE_URI, "about");
        if(aboutObj != null) { about = aboutObj.getChild(Document.class); }

        OAIRecord record = new OAIRecord(header, metadata, about);
        context.newObject(record);
        return record;
    }
}