/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.edm.EDM;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Jun 2017
 */
public class MongoEnrichmentConstants
{
    public static String FIELD_CODE_URI      = "codeUri";
    public static String FIELD_ORIG_CODE_URI = "originalCodeUri";
    public static String FIELD_LABEL         = "label";
    public static String FIELD_COREF         = "owlSameAs";

    public static String TBL_TERM_LIST = "TermList";
    public static String TBL_LOOKUP    = "lookup";
    public static String TBL_SEQUENCE  = "sequence";
    public static String TBL_PEOPLE    = "people";
    public static String TBL_CONCEPT   = "concept";
    public static String TBL_PLACE     = "place";
    public static String TBL_PERIOD    = "period";

    public static String SEQ_CONCEPT     = "nextConceptSequence";
    public static String SEQ_PLACE       = "nextPlaceSequence";
    public static String SEQ_AGENT       = "nextAgentSequence";
    public static String SEQ_TIME        = "nextTimespanSequence";

    public static final String PATTERN_STR
        = "^http[:][/][/]data[.]europeana[.]eu"
        + "[/](agent|place|time|concept)[/](\\w+)[/](\\d+)$";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR);

    public static Map<Resource,String> RES2TABLE = new HashMap();
    public static Map<String,Resource> TABLE2RES = new HashMap();

    static
    {
        RES2TABLE.put(EDM.Agent   , "people");
        RES2TABLE.put(EDM.Place   , "place");
        RES2TABLE.put(EDM.TimeSpan, "period");
        RES2TABLE.put(SKOS.Concept, "concept");

        TABLE2RES.put("people"    , EDM.Agent);
        TABLE2RES.put("place"     , EDM.Place);
        TABLE2RES.put("period"    , EDM.TimeSpan);
        TABLE2RES.put("concept"   , SKOS.Concept);
    }

    public static boolean isEntityURI(String uri)
    {
        return ( uri == null ? false : PATTERN.matcher(uri).matches() );
    }

    public static Long getEntityURISequence(String uri)
    {
        Matcher m = PATTERN.matcher(uri);
        return ( m.find() ? new Long(m.group(3)) : null );
    }
}
