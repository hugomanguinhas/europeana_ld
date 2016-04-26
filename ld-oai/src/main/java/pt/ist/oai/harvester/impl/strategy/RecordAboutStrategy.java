package pt.ist.oai.harvester.impl.strategy;

import java.util.*;

import pt.ist.oai.harvester.impl.*;
import pt.ist.xml.namespace.*;
import pt.ist.xml.parser.*;
import pt.ist.xml.parser.dom.*;

import static pt.ist.oai.harvester.OAIConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class RecordAboutStrategy<Context extends HarvesterContext> 
    extends DOMRootStrategy<Context>
{
    public void initStrategy(Map<QName,ParserStrategy<Context>> strats)
    {
        strats.put(new QName(NAMESPACE_URI, "about"), this);
    }
}
