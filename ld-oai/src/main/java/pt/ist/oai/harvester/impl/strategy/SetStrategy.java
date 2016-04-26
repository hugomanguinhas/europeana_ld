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
public class SetStrategy extends AbsParserStrategy<HarvesterContext>
{
    public void initStrategy(Map<QName,ParserStrategy<HarvesterContext>> strats)
    {
        DefaultParserStrategy<HarvesterContext> def
            = DefaultParserStrategy.getSingleton(HarvesterContext.class);
        strats.put(new QName(NAMESPACE_URI, "set")           , this);
        strats.put(new QName(NAMESPACE_URI, "setSpec")       , def);
        strats.put(new QName(NAMESPACE_URI, "setName")       , def);
        strats.put(new QName(NAMESPACE_URI, "setDescription"), def);
    }

    @Override
    public Object parse(ParserSupport support, HarvesterContext context)
           throws SAXException
    {
        ParsedObject obj   = support.getParsedObject();
        String setSpec     = getContent(obj, "setSpec");
        String name        = getContent(obj, "setName");
        String description = getContent(obj, "setDescription");
        OAIMetadataSet set = new OAIMetadataSet(setSpec, name, description);
        context.newObject(set);
        return set;
    }
}
