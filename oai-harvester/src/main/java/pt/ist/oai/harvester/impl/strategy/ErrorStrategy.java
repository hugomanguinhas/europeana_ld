package pt.ist.oai.harvester.impl.strategy;

import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class ErrorStrategy<Context extends ParserContext> 
    extends AbsParserStrategy<Context>
{
    public void initStrategy(Map<QName,ParserStrategy<Context>> strats)
    {
        strats.put(new QName(NAMESPACE_URI, "error"), this);
    }

    @Override
    public boolean ignoreCharacters() { return false; }

    @Override
    public Object parse(ParserSupport support, Context context)
           throws SAXException
    {
        ParsedObject obj = support.getParsedObject();
        String errorCode = obj.getAttributes().getAttributeNS(NAMESPACE_URI, "code");
        throw new ParsingException(OAIException.getException(errorCode, obj.getText()));
    }
}