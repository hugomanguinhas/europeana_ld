/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.edm.CC;
import eu.europeana.ld.edm.EBUCORE;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ODRL;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.RDAGR2;
import eu.europeana.ld.edm.SVCS;
import eu.europeana.ld.edm.WGS84;
import eu.europeana.ld.mongo.MongoClassDef.PropertyDef;
import eu.europeana.ld.mongo.MongoEDMParser.ParserContext;

import static eu.europeana.ld.iri.IRISupport.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public class MongoClassDef extends HashMap<String,PropertyDef>
{
    private static Map<String,MongoClassDef> _definitions   = new HashMap();

    public static String CLASS_AGGREGATION
        = "eu.europeana.corelib.solr.entity.AggregationImpl";
    public static String CLASS_EUROPEANA_AGGREGATION
        = "eu.europeana.corelib.solr.entity.EuropeanaAggregationImpl";
    public static String CLASS_PROVIDED_CHO
        = "eu.europeana.corelib.solr.entity.ProvidedCHOImpl";
    public static String CLASS_PROXY
        = "eu.europeana.corelib.solr.entity.ProxyImpl";
    public static String CLASS_WEBRESOURCE
        = "eu.europeana.corelib.solr.entity.WebResourceImpl";
    public static String CLASS_PLACE
        = "eu.europeana.corelib.solr.entity.PlaceImpl";
    public static String CLASS_AGENT
        = "eu.europeana.corelib.solr.entity.AgentImpl";
    public static String CLASS_TIMESPAN
        = "eu.europeana.corelib.solr.entity.TimespanImpl";
    public static String CLASS_CONCEPT
        = "eu.europeana.corelib.solr.entity.ConceptImpl";
    public static String CLASS_LICENSE
        = "eu.europeana.corelib.solr.entity.LicenseImpl";

    //Abbreviations used for the enrichment database
    public static String CLASS_PLACE_ABBR     = "PlaceImpl";
    public static String CLASS_AGENT_ABBR     = "AgentImpl";
    public static String CLASS_TIMESPAN_ABBR  = "TimespanImpl";
    public static String CLASS_CONCEPT_ABBR   = "ConceptImpl";

    static
    {
        MongoClassDef def;

        //Aggregations

        def = new MongoClassDef(ORE.Aggregation);
        def.put("edmDataProvider"                , newProp(EDM.dataProvider));
        def.put("edmIsShownBy"                   , newRef(EDM.isShownBy));
        def.put("edmIsShownAt"                   , newRef(EDM.isShownAt));
        def.put("edmObject"                      , newRef(EDM.object));
        def.put("edmProvider"                    , newProp(EDM.provider));
        def.put("edmRights"                      , newRef(EDM.rights));
        def.put("edmUgc"                         , newLiteral(EDM.ugc));
        def.put("dcRights"                       , newLiteral(DC.rights));
        def.put("hasView"                        , newRef(EDM.hasView));
        def.put("aggregatedCHO"                  , newRef(EDM.aggregatedCHO));
        def.put("aggregates"                     , newRef(ORE.aggregates));
        def.put("edmUnstored"                    , newLiteral(EDM.unstored));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_AGGREGATION, def);

        def = new MongoClassDef(EDM.EuropeanaAggregation);
        def.put("aggregatedCHO"                  , newRef(EDM.aggregatedCHO));
        def.put("aggregates"                     , newRef(ORE.aggregates));
        def.put("dcCreator"                      , newProp(DC.creator));
        def.put("edmLandingPage"                 , newRef(EDM.landingPage));
        def.put("edmIsShownBy"                   , newRef(EDM.isShownBy));
        def.put("edmHasView"                     , newRef(EDM.hasView));
        def.put("edmCountry"                     , newLiteral(EDM.country));
        def.put("edmLanguage"                    , newLiteral(EDM.language));
        def.put("edmRights"                      , newRef(EDM.rights));
        def.put("edmPreview"                     , newRef(EDM.preview));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_EUROPEANA_AGGREGATION, def);


        // Provided CHO & Proxy 

        def = new MongoClassDef(EDM.ProvidedCHO);
        def.put("owlSameAs"                      , newRef(OWL.sameAs));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_PROVIDED_CHO, def);

        def = new MongoClassDef(ORE.Proxy);
        def.put("dcContributor"                  , newProp(DC.contributor));
        def.put("dcCoverage"                     , newProp(DC.coverage));
        def.put("dcCreator"                      , newProp(DC.creator));
        def.put("dcDate"                         , newProp(DC.date));
        def.put("dcDescription"                  , newProp(DC.description));
        def.put("dcFormat"                       , newProp(DC.format));
        def.put("dcIdentifier"                   , newLiteral(DC.identifier));
        def.put("dcLanguage"                     , newLiteral(DC.language));
        def.put("dcPublisher"                    , newProp(DC.publisher));
        def.put("dcRelation"                     , newProp(DC.relation));
        def.put("dcRights"                       , newProp(DC.rights));
        def.put("dcSource"                       , newProp(DC.source));
        def.put("dcSubject"                      , newProp(DC.subject));
        def.put("dcTitle"                        , newLiteral(DC.title));
        def.put("dcType"                         , newRef(DC.type));
        def.put("dctermsAlternative"             , newLiteral(DCTerms.alternative));
        def.put("dctermsConformsTo"              , newProp(DCTerms.conformsTo));
        def.put("dctermsCreated"                 , newProp(DCTerms.created));
        def.put("dctermsExtent"                  , newProp(DCTerms.extent));
        def.put("dctermsHasFormat"               , newProp(DCTerms.hasFormat));
        def.put("dctermsHasPart"                 , newProp(DCTerms.hasPart));
        def.put("dctermsHasVersion"              , newProp(DCTerms.hasVersion));
        def.put("dctermsIsFormatOf"              , newProp(DCTerms.isFormatOf));
        def.put("dctermsIsPartOf"                , newProp(DCTerms.isPartOf));
        def.put("dctermsIsReferencedBy"          , newProp(DCTerms.isReferencedBy));
        def.put("dctermsIsReplacedBy"            , newProp(DCTerms.isReplacedBy));
        def.put("dctermsIsRequiredBy"            , newProp(DCTerms.isRequiredBy));
        def.put("dctermsIssued"                  , newProp(DCTerms.issued));
        def.put("dctermsIsVersionOf"             , newProp(DCTerms.isVersionOf));
        def.put("dctermsMedium"                  , newProp(DCTerms.medium));
        def.put("dctermsProvenance"              , newProp(DCTerms.provenance));
        def.put("dctermsReferences"              , newProp(DCTerms.references));
        def.put("dctermsReplaces"                , newProp(DCTerms.replaces));
        def.put("dctermsRequires"                , newProp(DCTerms.requires));
        def.put("dctermsSpatial"                 , newProp(DCTerms.spatial));
        def.put("dctermsTOC"                     , newProp(DCTerms.tableOfContents));
        def.put("dctermsTemporal"                , newProp(DCTerms.temporal));
        def.put("edmCurrentLocation"             , newRef(EDM.currentLocation));
        def.put("edmHasMet"                      , newRef(EDM.hasMet));
        def.put("edmHasType"                     , newProp(EDM.hasType));
        def.put("edmIncorporates"                , newRef(EDM.incorporates));
        def.put("edmIsDerivativeOf"              , newRef(EDM.isDerivativeOf));
        def.put("edmIsNextInSequence"            , newRef(EDM.isNextInSequence));
        def.put("edmIsRelatedTo"                 , newProp(EDM.isRelatedTo));
        def.put("edmIsRepresentationOf"          , newRef(EDM.isRepresentationOf));
        def.put("edmIsSimilarTo"                 , newRef(EDM.isSimilarTo));
        def.put("edmIsSuccessorOf"               , newRef(EDM.isSuccessorOf));
        def.put("edmRealizes"                    , newRef(EDM.realizes));
        def.put("edmType"                        , newLiteral(EDM.type));
        //edm:unstored (DEPRECATED)
        //edm:userTag (DEPRECATED)
        def.put("edmRights"                      , newRef(EDM.rights)); //should not be here
        def.put("edmWasPresentAt"                , newRef(EDM.wasPresentAt));
        def.put("europeanaProxy"                 , newLiteral(EDM.europeanaProxy));
        def.put("proxyIn"                        , newRef(ORE.proxyIn));
        def.put("proxyFor"                       , newRef(ORE.proxyFor));
        def.put("year"                           , newLiteral(EDM.year));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_PROXY, def);

        def = new MongoClassDef(EDM.WebResource);
        def.put("dcCreator"                      , newProp(DC.creator));
        def.put("dcDescription"                  , newProp(DC.description));
        def.put("dcFormat"                       , newProp(DC.format));
        def.put("webResourceDcRights"            , newProp(DC.rights));
        def.put("dcSource"                       , newProp(DC.source));
        def.put("dctermsConformsTo"              , newProp(DCTerms.conformsTo));
        def.put("dctermsCreated"                 , newProp(DCTerms.created));
        def.put("dctermsExtent"                  , newProp(DCTerms.extent));
        def.put("dctermsHasPart"                 , newRef(DCTerms.hasPart));
        def.put("dctermsIsFormatOf"              , newProp(DCTerms.isFormatOf));
        def.put("dctermsIsPartOf"                , newRef(DCTerms.isPartOf));
        def.put("dctermsIsReferencedBy"          , newProp(DCTerms.isReferencedBy));
        def.put("dctermsIssued"                  , newProp(DCTerms.issued));
        def.put("isNextInSequence"               , newRef(EDM.isNextInSequence));
        def.put("edmPreview"                     , newRef(EDM.preview));
        def.put("webResourceEdmRights"           , newRef(EDM.rights));
        def.put("owlSameAs"                      , newRef(OWL.sameAs));
        def.put("svcsHasService"                 , newRef(SVCS.has_service)); //Check?!?

        //Technical Metadata Properties
        def.put("resolution", new IgnoreProp());

        def.put("edmCodecName"                   , newLiteral(EDM.codecName));
        def.put("colorPalette"                   , newDT(EDM.componentColor, XSDDatatype.XSDhexBinary));
        def.put("colorSpace"                     , newLiteral(EDM.hasColorSpace));
        def.put("spatialResolution"              , newDT(EDM.spatialResolution, XSDDatatype.XSDnonNegativeInteger));
        def.put("audioChannelNumber"             , newDT(EBUCORE.audioChannelNumber, XSDDatatype.XSDnonNegativeInteger));
        def.put("bitRate"                        , newDT(EBUCORE.bitRate, XSDDatatype.XSDnonNegativeInteger));
        def.put("duration"                       , newLiteral(EBUCORE.duration));
        def.put("height"                         , newDT(EBUCORE.height, XSDDatatype.XSDinteger));
        def.put("fileSize"                       , newDT(EBUCORE.fileSize, XSDDatatype.XSDlong));
        def.put("frameRate"                      , newDT(EBUCORE.frameRate, XSDDatatype.XSDdouble));
        def.put("mimeType"                       , newLiteral(EBUCORE.hasMimeType));
        def.put("orientation"                    , newDT(EBUCORE.orientation, XSDDatatype.XSDstring, new OrientationProcessor()));
        def.put("sampleRate"                     , newDT(EBUCORE.sampleRate, XSDDatatype.XSDinteger));
        def.put("sampleSize"                     , newDT(EBUCORE.sampleSize, XSDDatatype.XSDinteger));
        def.put("width"                          , newDT(EBUCORE.width, XSDDatatype.XSDinteger));

        //alias
        def.put("codec"   , def.get("edmCodecName"));
        def.put("channels", def.get("audioChannelNumber"));
        def.put("bitDepth", def.get("sampleSize"));

        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_WEBRESOURCE, def);

        def = new MongoClassDef(EDM.Agent);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note));
        def.put("dcDate"                         , newProp(DC.date));
        def.put("dcIdentifier"                   , newLiteral(DC.identifier));
        def.put("hasPart"                        , newRef(DCTerms.hasPart));
        def.put("isPartOf"                       , newRef(DCTerms.isPartOf));
        def.put("begin"                          , newLiteral(EDM.begin));
        def.put("end"                            , newLiteral(EDM.end));
        def.put("edmWasPresentAt"                , newRef(EDM.wasPresentAt));
        def.put("edmHasMet"                      , newRef(EDM.hasMet));
        def.put("edmIsRelatedTo"                 , newRef(EDM.isRelatedTo));
        def.put("foafName"                       , newLiteral(FOAF.name));
        def.put("rdaGr2BiographicalInformation"  , newLiteral(RDAGR2.biographicalInformation));
        def.put("rdaGr2DateOfBirth"              , newLiteral(RDAGR2.dateOfBirth));
        def.put("rdaGr2DateOfDeath"              , newLiteral(RDAGR2.dateOfDeath));
        def.put("rdaGr2DateOfEstablishment"      , newLiteral(RDAGR2.dateOfEstablishment));
        def.put("rdaGr2DateOfTermination"        , newLiteral(RDAGR2.dateOfTermination));
        def.put("rdaGr2Gender"                   , newLiteral(RDAGR2.gender));
        def.put("rdaGr2PlaceOfBirth"             , newProp(RDAGR2.placeOfBirth));
        def.put("rdaGr2PlaceOfDeath"             , newProp(RDAGR2.placeOfDeath));
        def.put("rdaGr2ProfessionOrOccupation"   , newProp(RDAGR2.professionOrOccupation));
        def.put("owlSameAs"                      , newRef(OWL.sameAs));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_AGENT_ABBR, def);
        _definitions.put(CLASS_AGENT     , def);

        def = new MongoClassDef(EDM.Place);
        def.put("latitude"                       , newLiteral(WGS84.latitude));
        def.put("longitude"                      , newLiteral(WGS84.longitude));
        def.put("altitude"                       , newLiteral(WGS84.altitude));
        //def.put("lat"                            , WGS84.latitude);
        //def.put("long"                           , WGS84.longitude);
        //def.put("alt"                            , WGS84.altitude);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note));
        def.put("hasPart"                        , newRef(DCTerms.hasPart));
        def.put("isPartOf"                       , newRef(DCTerms.isPartOf));
        def.put("isNextInSequence"               , newRef(EDM.isNextInSequence));
        def.put("owlSameAs"                      , newRef(OWL.sameAs));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_PLACE_ABBR, def);
        _definitions.put(CLASS_PLACE     , def);

        def = new MongoClassDef(EDM.TimeSpan);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note));
        def.put("begin"                          , newLiteral(EDM.begin));
        def.put("end"                            , newLiteral(EDM.end));
        def.put("dctermsHasPart"                 , newRef(DCTerms.hasPart));
        def.put("hasPart"                        , newRef(DCTerms.hasPart));
        def.put("isPartOf"                       , newRef(DCTerms.isPartOf));
        def.put("isNextInSequence"               , newRef(EDM.isNextInSequence));
        def.put("owlSameAs"                      , newRef(OWL.sameAs));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_TIMESPAN_ABBR, def);
        _definitions.put(CLASS_TIMESPAN     , def);

        def = new MongoClassDef(SKOS.Concept);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note));
        def.put("broader"                        , newRef(SKOS.broader));
        def.put("narrower"                       , newRef(SKOS.narrower));
        def.put("related"                        , newRef(SKOS.related));
        def.put("broadMatch"                     , newRef(SKOS.broadMatch));
        def.put("narrowMatch"                    , newRef(SKOS.narrowMatch));
        def.put("relatedMatch"                   , newRef(SKOS.relatedMatch));
        def.put("exactMatch"                     , newRef(SKOS.exactMatch));
        def.put("closeMatch"                     , newRef(SKOS.closeMatch));
        def.put("notation"                       , newLiteral(SKOS.notation));
        def.put("inScheme"                       , newRef(SKOS.inScheme));
        def.put("owlSameAs"                      , newRef(OWL.sameAs));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_CONCEPT_ABBR, def);
        _definitions.put(CLASS_CONCEPT     , def);

        def = new MongoClassDef(CC.License);
        def.put("odrlInheritFrom"                , newProp(ODRL.inheritFrom));
        def.put("ccDeprecatedOn"                 , newRef(CC.deprecatedOn));
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put(CLASS_LICENSE, def);
    }

    public static MongoClassDef getDefinition(String name)
    {
        return _definitions.get(name);
    }

    private Resource           _type;
    private Map<String,String> _prefixes = new HashMap();

    public MongoClassDef(Resource type) { _type = type; }

    public Resource getType() { return _type; }

    public void setPrefix(String prefix, String ns)
    {
        _prefixes.put(prefix, ns);
    }

    public void setPrefixes(Map<String,String> prefixes)
    {
        _prefixes.putAll(prefixes);
    }

    public void setPrefixes(Model model)
    {
        for ( String prefix : _prefixes.keySet() )
        {
            model.setNsPrefix(prefix, _prefixes.get(prefix));
        }
    }

    private static PropertyDef newProp(Property p)
    {
        return new PropertyDef(p, null);
    }

    private static PropertyDef newProp(Property p, ValueProcessor processor)
    {
        return new PropertyDef(p, processor);
    }

    private static PropertyDef newDT(Property p, RDFDatatype dt)
    {
        return new DatatypeProp(p, dt, null);
    }

    private static PropertyDef newDT(Property p, RDFDatatype dt
                                   , ValueProcessor ps)
    {
        return new DatatypeProp(p, dt, ps);
    }

    private static PropertyDef newLiteral(Property p)
    {
        return new LiteralProp(p, null);
    }

    private static PropertyDef newRef(Property p)
    {
        return new ReferenceProp(p);
    }


    static interface ValueProcessor
    {
        public Object process(Object o);
    }

    static class OrientationProcessor implements ValueProcessor
    {
        @Override
        public Object process(Object o)
        {
            return ( o.toString().toLowerCase() );
        }
    }

    static class PropertyDef
    {
        protected Property       _prop;
        protected ValueProcessor _ps;

        public PropertyDef(Property prop, ValueProcessor ps)
        {
            _prop = prop;
            _ps   = ps;
        }

        public Property getProperty()       { return _prop; }

        public void newValue(Object o, ParserContext c)
        {
            createRef(o.toString(), c);
        }

        protected Object  process(Object o)
        {
            return ( _ps == null ? o : _ps.process(o) );
        }

        protected void createRef(String str, ParserContext c)
        {
            if ( !isAbsoluteIRI(str.trim()) ) { createLiteral(str, c); return; }
            c.getResource().addProperty(_prop, c.getModel().getResource(str));
        }

        protected void createLiteral(String str, ParserContext c)
        {
            Model  m = c.getModel();
            if ( c.hasLang() ) {
                c.getResource().addProperty(_prop
                                          , m.createLiteral(str, c.getLang()));
                return;
            }

            c.getResource().addProperty(_prop, m.createLiteral(str));
        }
    }

    static class IgnoreProp extends PropertyDef
    {
        public IgnoreProp() { super(null, null); }

        @Override
        public void newValue(Object o, ParserContext c) {}
    }

    static class ReferenceProp extends PropertyDef
    {
        public ReferenceProp(Property p) { super(p, null); }

        @Override
        public void newValue(Object o, ParserContext c)
        {
            if ( o == null ) { return; }
            createRef(o.toString(), c);
        }
    }

    static class LiteralProp extends PropertyDef
    {
        public LiteralProp(Property p, ValueProcessor ps) { super(p, ps); }

        @Override
        public void newValue(Object o, ParserContext c)
        {
            if ( o == null ) { return; }
            createLiteral(o.toString(), c);
        }
    }

    static class DatatypeProp extends PropertyDef
    {
        protected RDFDatatype _dt;

        public DatatypeProp(Property p, RDFDatatype dt, ValueProcessor pc)
        {
            super(p, pc);
            _dt = dt;
        }

        @Override
        public void newValue(Object o, ParserContext c)
        {
            if ( o == null ) { return; }

            Literal l = c.getModel().createTypedLiteral(process(o), _dt);
            c.getResource().addProperty(_prop, l);
        }
    }
}
