package pt.ist.oai.harvester.impl.strategy;

import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.impl.HarvesterContext;
import pt.ist.oai.harvester.impl.OAICmdInfoImpl;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class ResponseDateStrategy<Context extends HarvesterContext>
    extends AbsParserStrategy<Context>
{
    public void initStrategy(Map<QName,ParserStrategy<Context>> strats)
    {
        strats.put(new QName(NAMESPACE_URI, "responseDate"), this);
    }

    public boolean ignoreCharacters() { return false; }

    @Override
    public Object parse(ParserSupport support, Context context)
           throws SAXException
    {
        OAICmdInfoImpl info = (OAICmdInfoImpl)context.getInfo();
        if ( info.hasResponseDate() ) { return null; }

        ParsedObject   obj  = support.getParsedObject();
        Date           date = context.getNormalizedDate(obj.getText());
        if ( date != null ) { info.setResponseDate(date); }

        return null;
    }
}