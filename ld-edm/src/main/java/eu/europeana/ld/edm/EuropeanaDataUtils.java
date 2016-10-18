package eu.europeana.ld.edm;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Resource;

public class EuropeanaDataUtils
{
    public static final String NS     = "http://data.europeana.eu/";
    public static final String CHO_NS = NS + "item/";

    private static final Map<Resource,String> DATA_PREFIXES = new LinkedHashMap();

    static {
        DATA_PREFIXES.put(EDM.ProvidedCHO, "item");
        DATA_PREFIXES.put(ORE.Aggregation, "aggregation");
    }

    public static String getURIforCHO(String localID) { return CHO_NS + localID; }
}
