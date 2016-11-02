package eu.europeana.ld.edm;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;

import static org.apache.jena.rdf.model.ResourceFactory.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2016
 */
public class EDM 
{
    public static final String PREFIX = "edm";
    public static final String NS
        = "http://www.europeana.eu/schemas/edm/";

    public static final Resource ProvidedCHO          = createResource(NS + "ProvidedCHO");
    public static final Resource EuropeanaAggregation = createResource(NS + "EuropeanaAggregation");
    public static final Resource WebResource          = createResource(NS + "WebResource");
    public static final Resource Agent                = createResource(NS + "Agent");
    public static final Resource Place                = createResource(NS + "Place");
    public static final Resource TimeSpan             = createResource(NS + "TimeSpan");
    public static final Resource Event                = createResource(NS + "Event");
    public static final Resource PhysicalThing        = createResource(NS + "PhysicalThing");

    public static final Property about                = createProperty(RDF.getURI(), "about");
    public static final Property aggregatedCHO        = createProperty(NS, "aggregatedCHO");
    public static final Property begin                = createProperty(NS, "begin");
    public static final Property country              = createProperty(NS, "country");
    public static final Property collectionName       = createProperty(NS, "collectionName");
    public static final Property currentLocation      = createProperty(NS, "currentLocation");
    public static final Property dataProvider         = createProperty(NS, "dataProvider");
    public static final Property end                  = createProperty(NS, "end");
    public static final Property europeanaProxy       = createProperty(NS, "europeanaProxy");
    public static final Property hasMet               = createProperty(NS, "hasMet");
    public static final Property hasType              = createProperty(NS, "hasType");
    public static final Property hasView              = createProperty(NS, "hasView");
    public static final Property incorporates         = createProperty(NS, "incorporates");
    public static final Property intermediateProvider = createProperty(NS, "intermediateProvider");
    public static final Property isDerivativeOf       = createProperty(NS, "isDerivativeOf");
    public static final Property isNextInSequence     = createProperty(NS, "isNextInSequence");
    public static final Property isRelatedTo          = createProperty(NS, "isRelatedTo");
    public static final Property isRepresentationOf   = createProperty(NS, "isRepresentationOf");
    public static final Property isShownBy            = createProperty(NS, "isShownBy");
    public static final Property isShownAt            = createProperty(NS, "isShownAt");
    public static final Property isSimilarTo          = createProperty(NS, "isSimilarTo");
    public static final Property isSuccessorOf        = createProperty(NS, "isSuccessorOf");
    public static final Property landingPage          = createProperty(NS, "landingPage");
    public static final Property realizes             = createProperty(NS, "realizes");
    public static final Property rights               = createProperty(NS, "rights");
    public static final Property language             = createProperty(NS, "language");
    public static final Property object               = createProperty(NS, "object");
    public static final Property preview              = createProperty(NS, "preview");
    public static final Property provider             = createProperty(NS, "provider");
    public static final Property type                 = createProperty(NS, "type");
    public static final Property ugc                  = createProperty(NS, "ugc");
    @Deprecated
    public static final Property unstored             = createProperty(NS, "unstored");
    public static final Property wasPresentAt         = createProperty(NS, "wasPresentAt");
    public static final Property year                 = createProperty(NS, "year");

    //Technical Metadata
    public static final Property codecName            = createProperty(NS, "codecName");
    public static final Property componentColor       = createProperty(NS, "componentColor");
    public static final Property hasColorSpace        = createProperty(NS, "hasColorSpace");
    public static final Property spatialResolution    = createProperty(NS, "spatialResolution");

    public  static Resource[] CLASSES
        = { EDM.ProvidedCHO, ORE.Aggregation, EDM.EuropeanaAggregation
          , ORE.Proxy, EDM.WebResource
          , EDM.Place, EDM.Agent, EDM.TimeSpan, SKOS.Concept, CC.License };

    public static String[] NAMESPACES
        = { RDF.getURI(), RDFS.getURI(), EDM.NS, DC.NS, DCTerms.NS, FOAF.NS
          , ORE.NS, OWL.NS, RDAGR2.NS, SKOS.getURI(), ODRL.NS };

    public static Map<String,String> PREFIXES = new HashMap();

    public static Resource[] CONTEXTUAL_ENTITIES
        = { Place, Agent, SKOS.Concept, TimeSpan };

    static {
        PREFIXES.put("dc"          , DC.NS);
        PREFIXES.put("dcterms"     , DCTerms.NS);
        PREFIXES.put(EDM.PREFIX    , EDM.NS);
        PREFIXES.put("foaf"        , FOAF.NS);
        PREFIXES.put("owl"         , OWL.NS);
        PREFIXES.put(RDAGR2.PREFIX , RDAGR2.NS);
        PREFIXES.put("rdf"         , RDF.getURI());
        PREFIXES.put("rdfs"        , RDFS.getURI());
        PREFIXES.put("skos"        , SKOS.getURI());
        PREFIXES.put(WGS84.PREFIX  , WGS84.NS);
        PREFIXES.put("ore"         , ORE.NS);
        PREFIXES.put(EBUCORE.PREFIX, EBUCORE.NS);
        PREFIXES.put("xsd"         , XSD.NS);
        PREFIXES.put(CC.PREFIX     , CC.NS);
        PREFIXES.put(ODRL.PREFIX   , ODRL.NS);
    }
}
