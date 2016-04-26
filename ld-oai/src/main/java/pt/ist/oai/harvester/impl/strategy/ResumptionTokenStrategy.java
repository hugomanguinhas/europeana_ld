package pt.ist.oai.harvester.impl.strategy;

import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.impl.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.xml.AttributeList;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;

import static pt.ist.oai.harvester.OAIConstants.*;
import static pt.ist.oai.harvester.impl.HarvesterUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class ResumptionTokenStrategy<Context extends HarvesterContext>
    extends AbsParserStrategy<Context>
{
    public void initStrategy(Map<QName,ParserStrategy<Context>> strats)
    {
        strats.put(new QName(NAMESPACE_URI, "resumptionToken"), this);
    }

    @Override
    public boolean ignoreCharacters() { return false; }

    @Override
    public Object parse(ParserSupport support, Context context)
           throws SAXException
    {
        ParsedObject obj      = support.getParsedObject();
        AttributeList attrs   = obj.getAttributes();

        Date expirationDate   = context.getNormalizedDate(
            attrs.getAttributeNS(NAMESPACE_URI, "expirationDate"));
        Long completeListSize = asLong(
            attrs.getAttributeNS(NAMESPACE_URI, "completeListSize"));
        Long cursor           = asLong(
            attrs.getAttributeNS(NAMESPACE_URI, "cursor"));
        String token          = obj.getText();
        ResumptionToken resumptionToken = new ResumptionToken(
            token, expirationDate, cursor, completeListSize);

        context.newToken(resumptionToken);
        return null;
    }
}
