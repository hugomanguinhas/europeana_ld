package eu.europeana.vocs.wikidata;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import static org.apache.jena.rdf.model.ResourceFactory.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2016
 */
public class Wikidata
{
    public static Pattern PATTERN = Pattern.compile(".*wikidata[.]org.*");

    public static final String PREFIX       = "wd";
    public static final String PREFIX_WDS   = "wds";
    public static final String PREFIX_P     = "p";
    public static final String PREFIX_WDT   = "wdt";
    public static final String PREFIX_WDNO  = "wdno";
    public static final String PREFIX_PQV   = "pqv";
    public static final String PREFIX_PRV   = "prv";
    public static final String PREFIX_PSV   = "psv";
    public static final String PREFIX_WDATA = "wdata";
    public static final String PREFIX_WDREF = "wdref";

    public static final String NS       = "http://www.wikidata.org/entity/";
    public static final String NS_WDS   = "http://www.wikidata.org/entity/statement/";
    public static final String NS_P     = "http://www.wikidata.org/prop/";
    public static final String NS_WDT   = "http://www.wikidata.org/prop/direct/";
    public static final String NS_WDNO  = "http://www.wikidata.org/prop/novalue/";
    public static final String NS_PQV   = "http://www.wikidata.org/prop/qualifier/value/";
    public static final String NS_PRV   = "http://www.wikidata.org/prop/reference/value/";
    public static final String NS_PSV   = "http://www.wikidata.org/prop/statement/value/";
    public static final String NS_WDREF = "http://www.wikidata.org/reference/";
    public static final String NS_WDATA = "http://www.wikidata.org/wiki/Special:EntityData/";
    //@prefix ps:    <http://www.wikidata.org/prop/statement/> .

    public static String[] NAMESPACES
        = { NS, NS_WDS, NS_P, NS_WDT, NS_PSV, NS_PQV, NS_WDATA, NS_WDREF
          , NS_WDNO, NS_PRV };

    public static Map<String,String> PREFIXES = new HashMap();

    static {
        PREFIXES.put(PREFIX      , NS);
        PREFIXES.put(PREFIX_P    , NS_P);
        PREFIXES.put(PREFIX_WDNO , NS_WDNO);
        PREFIXES.put(PREFIX_PQV  , NS_PQV);
        PREFIXES.put(PREFIX_PRV  , NS_PRV);
        PREFIXES.put(PREFIX_PSV  , NS_PSV);
        PREFIXES.put(PREFIX_WDREF, NS_WDREF);
        PREFIXES.put(PREFIX_WDATA, NS_WDATA);
    }

    public static final Property P39  = createProperty(NS_WDT, "P39" ); //position held
    public static final Property P101 = createProperty(NS_WDT, "P101"); //field of work
    public static final Property P106 = createProperty(NS_WDT, "P106"); //occupation
}
