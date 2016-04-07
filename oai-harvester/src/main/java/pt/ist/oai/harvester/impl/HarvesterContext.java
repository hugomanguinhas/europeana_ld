package pt.ist.oai.harvester.impl;

import java.util.*;

import pt.ist.oai.harvester.model.*;
import pt.ist.xml.parser.*;

public interface HarvesterContext extends ParserContext
{
    public void newToken(ResumptionToken token);

    public Date getNormalizedDate(String datestamp);

    public void newObject(Object obj);
}
