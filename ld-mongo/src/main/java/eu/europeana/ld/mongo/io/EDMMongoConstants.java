/**
 * 
 */
package eu.europeana.ld.mongo.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.WGS84;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 3 Jan 2017
 */
public class EDMMongoConstants
{
    public static String FIELD_ID       = "id";
    public static String FIELD_TYPE     = "type";
    public static String FIELD_RESOURCE = "@res";
    public static String FIELD_LITERAL  = "@null";

    public static Collection<Property> PROP_SINGLETON
        = Arrays.asList(SKOS.prefLabel
                      , WGS84.longitude, WGS84.latitude, WGS84.altitude);

    public static boolean isPropSingleton(Property p)
    {
        return PROP_SINGLETON.contains(p);
    }
    
    /*
    public static Map<Property,String> EXCEPTIONS_2_LABEL = new LinkedHashMap();
    public static Map<String,Property> LABEL_2_EXCEPTIONS = new LinkedHashMap();
     */

    public static Map<Resource,String> CLASSES_2_LABEL = new LinkedHashMap();
    public static Map<String,Resource> LABEL_2_CLASSES = new LinkedHashMap();

    static
    {
/*
        EXCEPTIONS_2_LABEL.put(DC.rights , "dcRights");
        EXCEPTIONS_2_LABEL.put(DC.type   , "dcType");
        EXCEPTIONS_2_LABEL.put(EDM.rights, "edmRights");
        EXCEPTIONS_2_LABEL.put(EDM.type  , "edmType");
*/
        CLASSES_2_LABEL.put(EDM.ProvidedCHO, "cho");
        CLASSES_2_LABEL.put(EDM.WebResource, "webResources");
        CLASSES_2_LABEL.put(ORE.Aggregation, "aggregations");
        CLASSES_2_LABEL.put(ORE.Proxy      , "proxies");
        CLASSES_2_LABEL.put(EDM.Place      , "places");
        CLASSES_2_LABEL.put(EDM.Agent      , "agents");
        CLASSES_2_LABEL.put(EDM.TimeSpan   , "timespans");
        CLASSES_2_LABEL.put(SKOS.Concept   , "concepts");

        copyReversed(CLASSES_2_LABEL   , LABEL_2_CLASSES);
//        copyReversed(EXCEPTIONS_2_LABEL, LABEL_2_EXCEPTIONS);
    }

    private static <S,T> void copyReversed(Map<S,T> src, Map<T,S> dst)
    {
        for ( S s : src.keySet() ) { dst.put(src.get(s),s); }
    }
}
