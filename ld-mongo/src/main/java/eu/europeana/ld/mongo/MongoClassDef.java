/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.Collection;
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
import org.omg.CORBA.DATA_CONVERSION;

import eu.europeana.ld.edm.CC;
import eu.europeana.ld.edm.DOAP;
import eu.europeana.ld.edm.EBUCORE;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.EuropeanaDataUtils;
import eu.europeana.ld.edm.ODRL;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.RDAGR2;
import eu.europeana.ld.edm.SVCS;
import eu.europeana.ld.edm.WGS84;
import eu.europeana.ld.mongo.MongoClassDef.PropertyDef;
import eu.europeana.ld.mongo.MongoEDMParser.ParserContext;
import static eu.europeana.ld.iri.IRISupport.*;
import static eu.europeana.ld.mongo.MongoClassDef.JsonType.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public class MongoClassDef
{
    private static Map<String,MongoClassDef>   _java2defs  = new HashMap();
    private static Map<Resource,MongoClassDef> _class2defs = new HashMap();
    private static Map<String,String>          _classAbbr  = new HashMap();
    private static Map<String,String>          _col2class  = new HashMap();

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
    public static String CLASS_SERVICE
        = "eu.europeana.corelib.solr.entity.ServiceImpl";

    //Abbreviations used for the enrichment database
    public static String CLASS_PLACE_ABBR     = "PlaceImpl";
    public static String CLASS_AGENT_ABBR     = "AgentImpl";
    public static String CLASS_TIMESPAN_ABBR  = "TimespanImpl";
    public static String CLASS_CONCEPT_ABBR   = "ConceptImpl";

    static
    {
        _col2class.put("Aggregation"         , CLASS_AGGREGATION);
        _col2class.put("EuropeanaAggregation", CLASS_EUROPEANA_AGGREGATION);
        _col2class.put("Proxy"               , CLASS_PROXY);
        _col2class.put("WebResource"         , CLASS_WEBRESOURCE);
        _col2class.put("WebResourceMetaInfo" , CLASS_WEBRESOURCE);
        _col2class.put("Place"               , CLASS_PLACE);
        _col2class.put("Agent"               , CLASS_AGENT);
        _col2class.put("Timespan"            , CLASS_TIMESPAN);
        _col2class.put("Concept"             , CLASS_CONCEPT);
        _col2class.put("License"             , CLASS_LICENSE);
        _col2class.put("Service"             , CLASS_SERVICE);
        _col2class.put("ProvidedCHO"         , CLASS_PROVIDED_CHO);

        _classAbbr.put(CLASS_PLACE   , CLASS_PLACE_ABBR);
        _classAbbr.put(CLASS_AGENT   , CLASS_AGENT_ABBR);
        _classAbbr.put(CLASS_TIMESPAN, CLASS_TIMESPAN_ABBR);
        _classAbbr.put(CLASS_CONCEPT , CLASS_CONCEPT_ABBR);
        MongoClassDef def;

        //Aggregations

        def = new MongoClassDef(ORE.Aggregation, CLASS_AGGREGATION);
        def.put("edmDataProvider"                , newProp(EDM.dataProvider, "edmDataProvider", MAP));
        def.put("edmIsShownBy"                   , newRef(EDM.isShownBy, "edmIsShownBy", VALUE));
        def.put("edmIsShownAt"                   , newRef(EDM.isShownAt, "edmIsShownAt", VALUE));
        def.put("edmIntermediateProvider"        , newRef(EDM.intermediateProvider, "edmIntermediateProvider", MAP));
        def.put("edmObject"                      , newRef(EDM.object, "edmObject", VALUE));
        def.put("edmProvider"                    , newProp(EDM.provider, "edmProvider", MAP));
        def.put("edmRights"                      , newRef(EDM.rights, "edmRights", MAP));
        def.put("edmUgc"                         , newLiteral(EDM.ugc, "edmUgc", BOOLEAN));
        def.put("dcRights"                       , newLiteral(DC.rights, "dcRights", MAP));
        def.put("hasView"                        , newRef(EDM.hasView, "hasView", ARRAY));
        def.put("aggregatedCHO"                  , newIntRef(EDM.aggregatedCHO, "aggregatedCHO", VALUE, MongoEDMParser.DATA_NS + "/item"));
        def.put("aggregates"                     , newRef(ORE.aggregates, "aggregates", ARRAY));
        def.put("edmUnstored"                    , newLiteral(EDM.unstored, "edmUnstored", ARRAY));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_AGGREGATION, def);
        _class2defs.put(ORE.Aggregation , def);

        def = new MongoClassDef(EDM.EuropeanaAggregation
                              , CLASS_EUROPEANA_AGGREGATION);
        def.put("aggregatedCHO"                  , newIntRef(EDM.aggregatedCHO, "aggregatedCHO", VALUE, MongoEDMParser.DATA_NS + "/item"));
        def.put("aggregates"                     , newRef(ORE.aggregates, "aggregates", ARRAY));
        def.put("dcCreator"                      , newProp(DC.creator, "dcCreator", MAP));
        def.put("edmLandingPage"                 , newRef(EDM.landingPage, "edmLandingPage", VALUE));
        def.put("edmIsShownBy"                   , newRef(EDM.isShownBy, "edmIsShownBy", VALUE));
        def.put("edmHasView"                     , newRef(EDM.hasView, "edmHasView", ARRAY));
        def.put("edmCountry"                     , newLiteral(EDM.country, "edmCountry", MAP));
        def.put("edmLanguage"                    , newLiteral(EDM.language, "edmLanguage", MAP));
        def.put("edmRights"                      , newRef(EDM.rights, "edmRights", MAP));
        def.put("edmPreview"                     , newRef(EDM.preview, "edmPreview", VALUE));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_EUROPEANA_AGGREGATION, def);
        _class2defs.put(EDM.EuropeanaAggregation  , def);


        // Provided CHO & Proxy 

        def = new MongoClassDef(EDM.ProvidedCHO, CLASS_PROVIDED_CHO);
        def.put("owlSameAs"                      , newRef(OWL.sameAs, "owlSameAs", ARRAY));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_PROVIDED_CHO, def);
        _class2defs.put(EDM.ProvidedCHO  , def);

        def = new MongoClassDef(ORE.Proxy, CLASS_PROXY);
        def.put("dcContributor"                  , newProp(DC.contributor, "dcContributor", MAP));
        def.put("dcCoverage"                     , newProp(DC.coverage, "dcCoverage", MAP));
        def.put("dcCreator"                      , newProp(DC.creator, "dcCreator", MAP));
        def.put("dcDate"                         , newProp(DC.date, "dcDate", MAP));
        def.put("dcDescription"                  , newProp(DC.description, "dcDescription", MAP));
        def.put("dcFormat"                       , newProp(DC.format, "dcFormat", MAP));
        def.put("dcIdentifier"                   , newLiteral(DC.identifier, "dcIdentifier", MAP));
        def.put("dcLanguage"                     , newLiteral(DC.language, "dcLanguage", MAP));
        def.put("dcPublisher"                    , newProp(DC.publisher, "dcPublisher", MAP));
        def.put("dcRelation"                     , newProp(DC.relation, "dcRelation", MAP));
        def.put("dcRights"                       , newProp(DC.rights, "dcRights", MAP));
        def.put("dcSource"                       , newProp(DC.source, "dcSource", MAP));
        def.put("dcSubject"                      , newProp(DC.subject, "dcSubject", MAP));
        def.put("dcTitle"                        , newLiteral(DC.title, "dcTitle", MAP));
        def.put("dcType"                         , newRef(DC.type, "dcType", MAP));
        def.put("dctermsAlternative"             , newLiteral(DCTerms.alternative, "dctermsAlternative", MAP));
        def.put("dctermsConformsTo"              , newProp(DCTerms.conformsTo, "dctermsConformsTo", MAP));
        def.put("dctermsCreated"                 , newProp(DCTerms.created, "dctermsCreated", MAP));
        def.put("dctermsExtent"                  , newProp(DCTerms.extent, "dctermsExtent", MAP));
        def.put("dctermsHasFormat"               , newProp(DCTerms.hasFormat, "dctermsHasFormat", MAP));
        def.put("dctermsHasPart"                 , newProp(DCTerms.hasPart, "dctermsHasPart", MAP));
        def.put("dctermsHasVersion"              , newProp(DCTerms.hasVersion, "dctermsHasVersion", MAP));
        def.put("dctermsIsFormatOf"              , newProp(DCTerms.isFormatOf, "dctermsIsFormatOf", MAP));
        def.put("dctermsIsPartOf"                , newProp(DCTerms.isPartOf, "dctermsIsPartOf", MAP));
        def.put("dctermsIsReferencedBy"          , newProp(DCTerms.isReferencedBy, "dctermsIsReferencedBy", MAP));
        def.put("dctermsIsReplacedBy"            , newProp(DCTerms.isReplacedBy, "dctermsIsReplacedBy", MAP));
        def.put("dctermsIsRequiredBy"            , newProp(DCTerms.isRequiredBy, "dctermsIsRequiredBy", MAP));
        def.put("dctermsIssued"                  , newProp(DCTerms.issued, "dctermsIssued", MAP));
        def.put("dctermsIsVersionOf"             , newProp(DCTerms.isVersionOf, "dctermsIsVersionOf", MAP));
        def.put("dctermsMedium"                  , newProp(DCTerms.medium, "dctermsMedium", MAP));
        def.put("dctermsProvenance"              , newProp(DCTerms.provenance, "dctermsProvenance", MAP));
        def.put("dctermsReferences"              , newProp(DCTerms.references, "dctermsReferences", MAP));
        def.put("dctermsReplaces"                , newProp(DCTerms.replaces, "dctermsReplaces", MAP));
        def.put("dctermsRequires"                , newProp(DCTerms.requires, "dctermsRequires", MAP));
        def.put("dctermsSpatial"                 , newProp(DCTerms.spatial, "dctermsSpatial", MAP));
        def.put("dctermsTOC"                     , newProp(DCTerms.tableOfContents, "dctermsTOC", MAP));
        def.put("dctermsTemporal"                , newProp(DCTerms.temporal, "dctermsTemporal", MAP));
        def.put("edmCurrentLocation"             , newRef(EDM.currentLocation, "edmCurrentLocation", VALUE));
        def.put("edmHasMet"                      , newRef(EDM.hasMet, "edmHasMet", MAP));
        def.put("edmHasType"                     , newProp(EDM.hasType, "edmHasType", MAP));
        def.put("edmIncorporates"                , newRef(EDM.incorporates, "edmIncorporates", ARRAY));
        def.put("edmIsDerivativeOf"              , newRef(EDM.isDerivativeOf, "edmIsDerivativeOf", ARRAY));
        def.put("edmIsNextInSequence"            , newRef(EDM.isNextInSequence, "edmIsNextInSequence", ARRAY));
        def.put("edmIsRelatedTo"                 , newProp(EDM.isRelatedTo, "edmIsRelatedTo", MAP));
        def.put("edmIsRepresentationOf"          , newRef(EDM.isRepresentationOf, "edmIsRepresentationOf", VALUE));
        def.put("edmIsSimilarTo"                 , newRef(EDM.isSimilarTo, "edmIsSimilarTo", ARRAY));
        def.put("edmIsSuccessorOf"               , newRef(EDM.isSuccessorOf, "edmIsSuccessorOf", ARRAY));
        def.put("edmRealizes"                    , newRef(EDM.realizes, "edmRealizes", ARRAY));
        def.put("edmType"                        , newLiteral(EDM.type, "edmType", VALUE));
        //edm:unstored (DEPRECATED)
        //edm:userTag (DEPRECATED)
        def.put("edmRights"                      , newRef(EDM.rights, "edmRights", MAP)); //should not be here
        def.put("edmWasPresentAt"                , newRef(EDM.wasPresentAt, "edmWasPresentAt", ARRAY));
        def.put("europeanaProxy"                 , newLiteral(EDM.europeanaProxy, "europeanaProxy", BOOLEAN));
        def.put("proxyIn"                        , newRef(ORE.proxyIn, "proxyIn", ARRAY));
        def.put("proxyFor"                       , newIntRef(ORE.proxyFor, "proxyFor", VALUE, MongoEDMParser.DATA_NS + "/item"));
        def.put("year"                           , newLiteral(EDM.year, "year", VALUE));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_PROXY, def);
        _class2defs.put(ORE.Proxy , def);

        def = new MongoClassDef(EDM.WebResource, CLASS_WEBRESOURCE);
        def.put("dcCreator"                      , newProp(DC.creator, "dcCreator", MAP));
        def.put("dcDescription"                  , newProp(DC.description, "dcDescription", MAP));
        def.put("dcFormat"                       , newProp(DC.format, "dcFormat", MAP));
        def.put("webResourceDcRights"            , newProp(DC.rights, "webResourceDcRights", MAP));
        def.put("dcSource"                       , newProp(DC.source, "dcSource", MAP));
        def.put("dcType"                         , newRef(DC.type, "dcType", MAP));
        //dcType? def.put("dcType"                       , newProp(DC.type, MAP));
        def.put("dctermsConformsTo"              , newProp(DCTerms.conformsTo, "dctermsConformsTo", MAP));
        def.put("dctermsCreated"                 , newProp(DCTerms.created, "dctermsCreated", MAP));
        def.put("dctermsExtent"                  , newProp(DCTerms.extent, "dctermsExtent", MAP));
        def.put("dctermsHasPart"                 , newRef(DCTerms.hasPart, "dctermsHasPart", MAP));
        def.put("dctermsIsFormatOf"              , newProp(DCTerms.isFormatOf, "dctermsIsFormatOf", MAP));
        def.put("dctermsIsPartOf"                , newRef(DCTerms.isPartOf, "dctermsIsPartOf", MAP));
        def.put("dctermsIsReferencedBy"          , newProp(DCTerms.isReferencedBy, "dctermsIsReferencedBy", ARRAY));
        def.put("dctermsIssued"                  , newProp(DCTerms.issued, "dctermsIssued", MAP));
        def.put("isNextInSequence"               , newRef(EDM.isNextInSequence, "isNextInSequence", VALUE));
        def.put("edmPreview"                     , newRef(EDM.preview, "edmPreview", VALUE));
        def.put("webResourceEdmRights"           , newRef(EDM.rights, "webResourceEdmRights", MAP));
        def.put("owlSameAs"                      , newRef(OWL.sameAs, "owlSameAs", ARRAY));
        def.put("svcsHasService"                 , newRef(SVCS.has_service, "svcsHasService", ARRAY)); //Check?!?

        //Technical Metadata Properties
        def.put("resolution"  , new IgnoreProp());
        def.put("isSearchable", new IgnoreProp());

        def.put("edmCodecName"                   , newLiteral(EDM.codecName, "edmCodecName", VALUE));
        def.put("colorPalette"                   , newDT(EDM.componentColor, "colorPalette", XSDDatatype.XSDhexBinary, ARRAY));
        def.put("colorSpace"                     , newLiteral(EDM.hasColorSpace, "colorSpace", VALUE));
        def.put("spatialResolution"              , newDT(EDM.spatialResolution, "spatialResolution", XSDDatatype.XSDnonNegativeInteger, VALUE));
        //missing audioChannelNumber from CoreLib class
        def.put("audioChannelNumber"             , newDT(EBUCORE.audioChannelNumber, "audioChannelNumber", XSDDatatype.XSDnonNegativeInteger, VALUE));
        def.put("bitRate"                        , newDT(EBUCORE.bitRate, "bitRate", XSDDatatype.XSDnonNegativeInteger, VALUE));
        def.put("duration"                       , newLiteral(EBUCORE.duration, "duration", VALUE));
        def.put("height"                         , newDT(EBUCORE.height, "height", XSDDatatype.XSDinteger, VALUE));
        def.put("fileSize"                       , newDT(EBUCORE.fileByteSize, "fileByteSize", XSDDatatype.XSDlong, VALUE));
        def.put("frameRate"                      , newDT(EBUCORE.frameRate, "frameRate", XSDDatatype.XSDdouble, VALUE));
        def.put("mimeType"                       , newLiteral(EBUCORE.hasMimeType, "mimeType", VALUE));
        def.put("orientation"                    , newDT(EBUCORE.orientation, "orientation", XSDDatatype.XSDstring, new OrientationProcessor(), VALUE));
        def.put("sampleRate"                     , newDT(EBUCORE.sampleRate, "sampleRate", XSDDatatype.XSDinteger, VALUE));
        def.put("sampleSize"                     , newDT(EBUCORE.sampleSize, "sampleSize", XSDDatatype.XSDinteger, VALUE));
        def.put("width"                          , newDT(EBUCORE.width, "width", XSDDatatype.XSDinteger, VALUE));

        //alias
        def.put("codec"   , def.get("edmCodecName"));
        def.put("channels", def.get("audioChannelNumber"));
        def.put("bitDepth", def.get("sampleSize"));

        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_WEBRESOURCE, def);
        _class2defs.put(EDM.WebResource , def);

        def = new MongoClassDef(EDM.Agent, CLASS_AGENT);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel, "prefLabel", MAP));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel, "altLabel", MAP));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel, "hiddenLabel", MAP)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note, "note", MAP));
        def.put("dcDate"                         , newProp(DC.date, "dcDate", MAP));
        def.put("dcIdentifier"                   , newLiteral(DC.identifier, "dcIdentifier", MAP));
        //hasPart and isPartOf missing from CoreLib
        def.put("hasPart"                        , newRef(DCTerms.hasPart, "hasPart", MAP));
        def.put("isPartOf"                       , newRef(DCTerms.isPartOf, "isPartOf", MAP));
        def.put("begin"                          , newLiteral(EDM.begin, "begin", MAP));
        def.put("end"                            , newLiteral(EDM.end, "end", MAP));
        def.put("edmWasPresentAt"                , newRef(EDM.wasPresentAt, "edmWasPresentAt", ARRAY));
        def.put("edmHasMet"                      , newRef(EDM.hasMet, "edmHasMet", MAP));
        def.put("edmIsRelatedTo"                 , newRef(EDM.isRelatedTo, "edmIsRelatedTo", MAP));
        def.put("foafName"                       , newLiteral(FOAF.name, "foafName", MAP));
        def.put("rdaGr2BiographicalInformation"  , newLiteral(RDAGR2.biographicalInformation, "rdaGr2BiographicalInformation", MAP));
        def.put("rdaGr2DateOfBirth"              , newLiteral(RDAGR2.dateOfBirth, "rdaGr2DateOfBirth", MAP));
        def.put("rdaGr2DateOfDeath"              , newLiteral(RDAGR2.dateOfDeath, "rdaGr2DateOfDeath", MAP));
        def.put("rdaGr2DateOfEstablishment"      , newLiteral(RDAGR2.dateOfEstablishment, "rdaGr2DateOfEstablishment", MAP));
        def.put("rdaGr2DateOfTermination"        , newLiteral(RDAGR2.dateOfTermination, "rdaGr2DateOfTermination", MAP));
        def.put("rdaGr2Gender"                   , newLiteral(RDAGR2.gender, "rdaGr2Gender", MAP));
        def.put("rdaGr2PlaceOfBirth"             , newProp(RDAGR2.placeOfBirth, "rdaGr2PlaceOfBirth", MAP));
        def.put("rdaGr2PlaceOfDeath"             , newProp(RDAGR2.placeOfDeath, "rdaGr2PlaceOfDeath", MAP));
        def.put("rdaGr2ProfessionOrOccupation"   , newProp(RDAGR2.professionOrOccupation, "rdaGr2ProfessionOrOccupation", MAP));
        def.put("owlSameAs"                      , newRef(OWL.sameAs, "owlSameAs", ARRAY));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_AGENT_ABBR, def);
        _java2defs.put(CLASS_AGENT     , def);
        _class2defs.put(EDM.Agent      , def);

        def = new MongoClassDef(EDM.Place, CLASS_PLACE);
        def.put("latitude"                       , newLiteral(WGS84.latitude, "latitude", VALUE));
        def.put("longitude"                      , newLiteral(WGS84.longitude, "longitude", VALUE));
        def.put("altitude"                       , newLiteral(WGS84.altitude, "altitude", VALUE));
        //def.put("lat"                            , WGS84.latitude);
        //def.put("long"                           , WGS84.longitude);
        //def.put("alt"                            , WGS84.altitude);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel, "prefLabel", MAP));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel, "altLabel", MAP));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel, "hiddenLabel", MAP)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note, "note", MAP));
        def.put("hasPart"                        , newRef(DCTerms.hasPart, "hasPart", MAP));
        def.put("isPartOf"                       , newRef(DCTerms.isPartOf, "isPartOf", MAP));
        def.put("dcTermsHasPart"                 , newRef(DCTerms.hasPart, "hasPart", MAP));
        //isNextInSequence missing from CoreLib
        def.put("isNextInSequence"               , newRef(EDM.isNextInSequence, "isNextInSequence", MAP));
        def.put("owlSameAs"                      , newRef(OWL.sameAs, "owlSameAs", ARRAY));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_PLACE_ABBR, def);
        _java2defs.put(CLASS_PLACE     , def);
        _class2defs.put(EDM.Place      , def);

        def = new MongoClassDef(EDM.TimeSpan, CLASS_TIMESPAN);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel, "prefLabel", MAP));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel, "altLabel", MAP));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel, "hiddenLabel", MAP)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note, "note", MAP));
        def.put("begin"                          , newLiteral(EDM.begin, "begin", MAP));
        def.put("end"                            , newLiteral(EDM.end, "end", MAP));
        def.put("dctermsHasPart"                 , newRef(DCTerms.hasPart, "dctermsHasPart", MAP));
        def.put("hasPart"                        , newRef(DCTerms.hasPart, "hasPart", MAP));
        def.put("isPartOf"                       , newRef(DCTerms.isPartOf, "isPartOf", MAP));
        //isNextInSequence missing from CoreLib
        def.put("isNextInSequence"               , newRef(EDM.isNextInSequence, "isNextInSequence", MAP));
        def.put("owlSameAs"                      , newRef(OWL.sameAs, "owlSameAs", ARRAY));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_TIMESPAN_ABBR, def);
        _java2defs.put(CLASS_TIMESPAN     , def);
        _class2defs.put(EDM.TimeSpan      , def);

        def = new MongoClassDef(SKOS.Concept, CLASS_CONCEPT);
        def.put("prefLabel"                      , newLiteral(SKOS.prefLabel, "prefLabel", MAP));
        def.put("altLabel"                       , newLiteral(SKOS.altLabel, "altLabel", MAP));
        def.put("hiddenLabel"                    , newLiteral(SKOS.hiddenLabel, "hiddenLabel", MAP)); //Deprecated
        def.put("note"                           , newLiteral(SKOS.note, "note", MAP));
        def.put("broader"                        , newRef(SKOS.broader, "broader", ARRAY));
        def.put("narrower"                       , newRef(SKOS.narrower, "narrower", ARRAY));
        def.put("related"                        , newRef(SKOS.related, "related", ARRAY));
        def.put("broadMatch"                     , newRef(SKOS.broadMatch, "broadMatch", ARRAY));
        def.put("narrowMatch"                    , newRef(SKOS.narrowMatch, "narrowMatch", ARRAY));
        def.put("relatedMatch"                   , newRef(SKOS.relatedMatch, "relatedMatch", ARRAY));
        def.put("exactMatch"                     , newRef(SKOS.exactMatch, "exactMatch", ARRAY));
        def.put("closeMatch"                     , newRef(SKOS.closeMatch, "closeMatch", ARRAY));
        def.put("notation"                       , newLiteral(SKOS.notation, "notation", MAP));
        def.put("inScheme"                       , newRef(SKOS.inScheme, "inScheme", ARRAY));
        def.put("owlSameAs"                      , newRef(OWL.sameAs, "owlSameAs", ARRAY));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_CONCEPT_ABBR, def);
        _java2defs.put(CLASS_CONCEPT     , def);
        _class2defs.put(SKOS.Concept     , def);

        def = new MongoClassDef(CC.License, CLASS_LICENSE);
        def.put("odrlInheritFrom"                , newProp(ODRL.inheritFrom, "odrlInheritFrom", VALUE));
        def.put("ccDeprecatedOn"                 , newRef(CC.deprecatedOn, "ccDeprecatedOn", VALUE));
        def.setPrefixes(EDM.PREFIXES);
        _java2defs.put(CLASS_LICENSE, def);
        _class2defs.put(CC.License  , def);

        def = new MongoClassDef(SVCS.Service, CLASS_SERVICE);
        def.setPrefixes(EDM.PREFIXES);
        def.put("dctermsConformsTo"              , newProp(DCTerms.conformsTo, "dctermsConformsTo", ARRAY));
        def.put("doapImplements"                 , newRef(DOAP.impls, "doapImplements", VALUE));
        _java2defs.put(CLASS_SERVICE , def);
        _class2defs.put(SVCS.Service, def);
    }

    public static String getJavaClassAbbr(String className)
    {
        String abbr = _classAbbr.get(className);
        return ( abbr == null ? className : abbr );
    }

    public static MongoClassDef getDefinition(String name)
    {
        return _java2defs.get(name);
    }

    public static MongoClassDef getDefinition(Resource type)
    {
        return _class2defs.get(type);
    }

    public static Collection<MongoClassDef> getDefinitions()
    {
        return _class2defs.values();
    }

    public static String getClassFromCollection(String col)
    {
        return _col2class.get(col);
    }

    private Resource                  _type;
    private String                    _javaClass;
    private Map<String,String>        _prefixes = new HashMap();
    private Map<String,PropertyDef>   _propsByName = new HashMap();
    private Map<Property,PropertyDef> _propsByURI  = new HashMap();
    
    public MongoClassDef(Resource type, String javaClass)
    {
        _type      = type;
        _javaClass = javaClass;
    }

    public Resource getType()      { return _type;      }
    public String   getJavaClass() { return _javaClass; }

    public PropertyDef get(String   name) { return _propsByName.get(name); }
    public PropertyDef get(Property p   ) { return _propsByURI.get(p);     }

    public void put(String name, PropertyDef prop)
    {
        _propsByName.put(name, prop);
        _propsByURI.put(prop.getProperty(), prop);
    }

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

    private static PropertyDef newProp(Property p, String propLabel
                                     , JsonType jsonType)
    {
        return new PropertyDef(p, propLabel, null, jsonType);
    }

    private static PropertyDef newProp(Property p, String propLabel
                                     , ValueProcessor processor
                                     , JsonType jsonType)
    {
        return new PropertyDef(p, propLabel, processor, jsonType);
    }

    private static PropertyDef newDT(Property p, String propLabel
                                   , RDFDatatype dt, JsonType jsonType)
    {
        return new DatatypeProp(p, propLabel, dt, null, jsonType);
    }

    private static PropertyDef newDT(Property p, String propLabel
                                   , RDFDatatype dt, ValueProcessor ps
                                   , JsonType jsonType)
    {
        return new DatatypeProp(p, propLabel, dt, ps, jsonType);
    }

    private static PropertyDef newLiteral(Property p, String propLabel
                                        , JsonType jsonType)
    {
        return new LiteralProp(p, propLabel, null, jsonType);
    }

    private static PropertyDef newRef(Property p, String propLabel
                                    , JsonType jsonType)
    {
        return new ReferenceProp(p, propLabel, jsonType);
    }

    private static PropertyDef newIntRef(Property p, String propLabel
                                       , JsonType jsonType, String prefix)
    {
        return new IntReferenceProp(p, propLabel, jsonType, prefix);
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

    public static enum JsonType
    {
        ARRAY, MAP, VALUE, BOOLEAN
    }

    public static class PropertyDef
    {
        protected Property       _prop;
        protected String         _propLabel;
        protected ValueProcessor _ps;
        protected JsonType       _jsonType;

        public PropertyDef(Property prop, String propLabel
                         , ValueProcessor ps, JsonType jsonType)
        {
            _prop      = prop;
            _propLabel = propLabel;
            _ps        = ps;
            _jsonType  = jsonType;
        }

        public Property getProperty()      { return _prop;      }
        public String   getPropertyLabel() { return _propLabel; }
        public JsonType getJsonType()      { return _jsonType;  }

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
        public IgnoreProp() { super(null, null, null, null); }

        @Override
        public void newValue(Object o, ParserContext c) {}
    }

    static class ReferenceProp extends PropertyDef
    {
        public ReferenceProp(Property p, String propLabel, JsonType jsonType)
        { 
            super(p, propLabel, null, jsonType);
        }

        @Override
        public void newValue(Object o, ParserContext c)
        {
            if ( o == null ) { return; }
            createRef(o.toString(), c);
        }
    }

    static class IntReferenceProp extends PropertyDef
    {
        private String _prefix;

        public IntReferenceProp(Property p, String propLabel
                              , JsonType jsonType, String prefix)
        { 
            super(p, propLabel, null, jsonType);
            _prefix = prefix;
        }

        @Override
        public void newValue(Object o, ParserContext c)
        {
            if ( o == null ) { return; }

            String str = o.toString();
            if ( !str.startsWith("http://") )
            {
                if ( str.startsWith("/item") ) { str = str.substring(5); }
                str = _prefix + str;
            }

            c.getResource().addProperty(_prop, c.getModel().getResource(str));
        }
    }

    static class LiteralProp extends PropertyDef
    {
        public LiteralProp(Property p, String propLabel
                         , ValueProcessor ps, JsonType jsonType)
        {
            super(p, propLabel, ps, jsonType);
        }

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

        public DatatypeProp(Property p, String propLabel
                          , RDFDatatype dt, ValueProcessor pc
                          , JsonType jsonType)
        {
            super(p, propLabel, pc, jsonType);
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
