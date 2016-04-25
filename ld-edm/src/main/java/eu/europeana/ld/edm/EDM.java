package eu.europeana.ld.edm;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

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

    public static final Property about       = createProperty(RDF.getURI(), "about");
    public static final Property begin       = createProperty(NS, "begin");
    public static final Property end         = createProperty(NS, "end");
    public static final Property hasMet      = createProperty(NS, "hasMet");
    public static final Property isRelatedTo = createProperty(NS, "isRelatedTo");
    public static final Property language    = createProperty(NS, "language");
    public static final Property year        = createProperty(NS, "year");

    public  static Resource[] CLASSES
        = { EDM.ProvidedCHO, ORE.Aggregation, EDM.EuropeanaAggregation
          , ORE.Proxy, EDM.Place, EDM.Agent, EDM.TimeSpan, SKOS.Concept };

    public static String[] NAMESPACES
        = { RDF.getURI(), EDM.NS, DC.NS, DCTerms.NS, FOAF.NS, ORE.NS, OWL.NS
          , RDAGR2.NS, SKOS.getURI() };

    public static Resource[] CONTEXTUAL_ENTITIES
        = { Place, Agent, SKOS.Concept, TimeSpan };
}
