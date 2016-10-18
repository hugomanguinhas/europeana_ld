/**
 * 
 */
package eu.europeana.ld.mongo;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
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

import eu.europeana.ld.edm.EBUCORE;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.RDAGR2;
import eu.europeana.ld.edm.WGS84;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public class MongoClassDef extends HashMap<String,Property>
{
    private static Map<String,MongoClassDef> _definitions   = new HashMap();
    private static Map<Property,RDFDatatype> _propDatatypes = new HashMap();

    static
    {
        MongoClassDef def;

        //Aggregations

        def = new MongoClassDef(ORE.Aggregation);
        def.put("edmDataProvider"                , EDM.dataProvider);
        def.put("edmIsShownBy"                   , EDM.isShownBy);
        def.put("edmIsShownAt"                   , EDM.isShownAt);
        def.put("edmObject"                      , EDM.object);
        def.put("edmProvider"                    , EDM.provider);
        def.put("edmRights"                      , EDM.rights);
        def.put("edmUgc"                         , EDM.ugc);
        def.put("dcRights"                       , DC.rights);
        def.put("hasView"                        , EDM.hasView);
        def.put("aggregatedCHO"                  , EDM.aggregatedCHO);
        def.put("aggregates"                     , ORE.aggregates);
        def.put("edmUnstored"                    , EDM.unstored);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("eu.europeana.corelib.solr.entity.AggregationImpl", def);


        def = new MongoClassDef(EDM.EuropeanaAggregation);
        def.put("aggregatedCHO"                  , EDM.aggregatedCHO);
        def.put("aggregates"                     , ORE.aggregates);
        def.put("dcCreator"                      , DC.creator);
        def.put("edmLandingPage"                 , EDM.landingPage);
        def.put("edmIsShownBy"                   , EDM.isShownBy);
        def.put("edmHasView"                     , EDM.hasView);
        def.put("edmCountry"                     , EDM.country);
        def.put("edmLanguage"                    , EDM.language);
        def.put("edmRights"                      , EDM.rights);
        def.put("edmPreview"                     , EDM.preview);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("eu.europeana.corelib.solr.entity.EuropeanaAggregationImpl", def);


        // Provided CHO & Proxy 

        def = new MongoClassDef(EDM.ProvidedCHO);
        def.put("owlSameAs"                      , OWL.sameAs);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("eu.europeana.corelib.solr.entity.ProvidedCHOImpl", def);

        def = new MongoClassDef(ORE.Proxy);
        def.put("dcContributor"                  , DC.contributor);
        def.put("dcCoverage"                     , DC.coverage);
        def.put("dcCreator"                      , DC.creator);
        def.put("dcDate"                         , DC.date);
        def.put("dcDescription"                  , DC.description);
        def.put("dcFormat"                       , DC.format);
        def.put("dcIdentifier"                   , DC.identifier);
        def.put("dcLanguage"                     , DC.language);
        def.put("dcPublisher"                    , DC.publisher);
        def.put("dcRelation"                     , DC.relation);
        def.put("dcRights"                       , DC.rights);
        def.put("dcSource"                       , DC.source);
        def.put("dcSubject"                      , DC.subject);
        def.put("dcTitle"                        , DC.title);
        def.put("dcType"                         , DC.type);
        def.put("dctermsAlternative"             , DCTerms.alternative);
        def.put("dctermsConformsTo"              , DCTerms.conformsTo);
        def.put("dctermsCreated"                 , DCTerms.created);
        def.put("dctermsExtent"                  , DCTerms.extent);
        def.put("dctermsHasFormat"               , DCTerms.hasFormat);
        def.put("dctermsHasPart"                 , DCTerms.hasPart);
        def.put("dctermsHasVersion"              , DCTerms.hasVersion);
        def.put("dctermsIsFormatOf"              , DCTerms.isFormatOf);
        def.put("dctermsIsPartOf"                , DCTerms.isPartOf);
        def.put("dctermsIsReferencedBy"          , DCTerms.isReferencedBy);
        def.put("dctermsIsReplacedBy"            , DCTerms.isRequiredBy);
        def.put("dctermsIsRequiredBy"            , DCTerms.isRequiredBy);
        def.put("dctermsIssued"                  , DCTerms.issued);
        def.put("dctermsIsVersionOf"             , DCTerms.isVersionOf);
        def.put("dctermsMedium"                  , DCTerms.medium);
        def.put("dctermsProvenance"              , DCTerms.provenance);
        def.put("dctermsReferences"              , DCTerms.references);
        def.put("dctermsReplaces"                , DCTerms.replaces);
        def.put("dctermsRequires"                , DCTerms.requires);
        def.put("dctermsSpatial"                 , DCTerms.spatial);
        def.put("dctermsTOC"                     , DCTerms.tableOfContents);
        def.put("dctermsTemporal"                , DCTerms.temporal);
        def.put("edmCurrentLocation"             , EDM.currentLocation);
        def.put("edmHasMet"                      , EDM.hasMet);
        def.put("edmHasType"                     , EDM.hasType);
        def.put("edmIncorporates"                , EDM.incorporates);
        def.put("edmIsDerivativeOf"              , EDM.isDerivativeOf);
        def.put("edmIsNextInSequence"            , EDM.isNextInSequence);
        def.put("edmIsRelatedTo"                 , EDM.isRelatedTo);
        def.put("edmIsRepresentationOf"          , EDM.isRepresentationOf);
        def.put("edmIsSimilarTo"                 , EDM.isSimilarTo);
        def.put("edmIsSuccessorOf"               , EDM.isSuccessorOf);
        def.put("edmRealizes"                    , EDM.realizes);
        def.put("edmType"                        , EDM.type);
        def.put("edmRights"                      , EDM.rights);
        def.put("edmWasPresentAt"                , EDM.wasPresentAt);
        def.put("europeanaProxy"                 , EDM.europeanaProxy);
        def.put("proxyIn"                        , ORE.proxyIn);
        def.put("proxyFor"                       , ORE.proxyFor);
        def.put("year"                           , EDM.year);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("eu.europeana.corelib.solr.entity.ProxyImpl", def);

        def = new MongoClassDef(EDM.WebResource);
//        def.put("dcContributor"                  , DC.contributor);
        def.put("dcCreator"                      , DC.creator);
        def.put("dcDescription"                  , DC.description);
        def.put("dcFormat"                       , DC.format);
        def.put("dcSource"                       , DC.source);
        def.put("dctermsConformsTo"              , DCTerms.conformsTo);
        def.put("dctermsCreated"                 , DCTerms.created);
        def.put("dctermsExtent"                  , DCTerms.extent);
        def.put("dctermsHasPart"                 , DCTerms.hasPart);
        def.put("dctermsIsFormatOf"              , DCTerms.isFormatOf);
        def.put("isNextInSequence"               , EDM.isNextInSequence);
        def.put("owlSameAs"                      , OWL.sameAs);
        def.put("webResourceDcRights"            , DC.rights);
        def.put("webResourceEdmRights"           , EDM.rights);

        //Technical Metadata Properties
        def.put("edmCodecName"                   , EDM.codecName);
        def.put("colorPalette"                   , EDM.componentColor);
        def.put("colorSpace"                     , EDM.hasColorSpace);
        def.put("spatialResolution"              , EDM.spatialResolution);
        def.put("audioChannelNumber"             , EBUCORE.audioChannelNumber);
        def.put("bitRate"                        , EBUCORE.bitRate);
        def.put("duration"                       , EBUCORE.duration);
        def.put("height"                         , EBUCORE.height);
        def.put("fileSize"                       , EBUCORE.fileSize);
        def.put("frameRate"                      , EBUCORE.frameRate);
        def.put("mimeType"                       , EBUCORE.hasMimeType);
        def.put("orientation"                    , EBUCORE.orientation);
        def.put("sampleRate"                     , EBUCORE.sampleRate);
        def.put("sampleSize"                     , EBUCORE.sampleSize);
        def.put("width"                          , EBUCORE.width);

        _propDatatypes.put(EDM.componentColor        , XSDDatatype.XSDhexBinary);
        _propDatatypes.put(EDM.spatialResolution     , XSDDatatype.XSDnonNegativeInteger);
        _propDatatypes.put(EBUCORE.audioChannelNumber, XSDDatatype.XSDnonNegativeInteger);
        _propDatatypes.put(EBUCORE.bitRate           , XSDDatatype.XSDnonNegativeInteger);
        //_propDatatypes.put(EBUCORE.duration);
        _propDatatypes.put(EBUCORE.height            , XSDDatatype.XSDinteger);
        _propDatatypes.put(EBUCORE.fileSize          , XSDDatatype.XSDlong);
        _propDatatypes.put(EBUCORE.frameRate         , XSDDatatype.XSDdouble);
        _propDatatypes.put(EBUCORE.orientation       , XSDDatatype.XSDstring);
        _propDatatypes.put(EBUCORE.sampleRate        , XSDDatatype.XSDinteger);
        _propDatatypes.put(EBUCORE.sampleSize        , XSDDatatype.XSDinteger);
        _propDatatypes.put(EBUCORE.width             , XSDDatatype.XSDinteger);

        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("eu.europeana.corelib.solr.entity.WebResourceImpl", def);

        def = new MongoClassDef(EDM.Agent);
        def.put("prefLabel"                      , SKOS.prefLabel);
        def.put("altLabel"                       , SKOS.altLabel);
        def.put("hiddenLabel"                    , SKOS.hiddenLabel);
        def.put("note"                           , SKOS.note);
        def.put("begin"                          , EDM.begin);
        def.put("end"                            , EDM.end);
        def.put("edmWasPresentAt"                , EDM.wasPresentAt);
        def.put("edmHasMet"                      , EDM.hasMet);
        def.put("edmIsRelatedTo"                 , EDM.isRelatedTo);
        def.put("foafName"                       , FOAF.name);
        def.put("dcDate"                         , DC.date);
        def.put("dcIdentifier"                   , DC.identifier);
        def.put("hasPart"                        , DCTerms.hasPart);
        def.put("isPartOf"                       , DCTerms.isPartOf);
        def.put("rdaGr2BiographicalInformation"  , RDAGR2.biographicalInformation);
        def.put("rdaGr2DateOfBirth"              , RDAGR2.dateOfBirth);
        def.put("rdaGr2DateOfDeath"              , RDAGR2.dateOfDeath);
        def.put("rdaGr2DateOfEstablishment"      , RDAGR2.dateOfEstablishment);
        def.put("rdaGr2DateOfTermination"        , RDAGR2.dateOfTermination);
        def.put("rdaGr2Gender"                   , RDAGR2.gender);
        def.put("rdaGr2PlaceOfBirth"             , RDAGR2.placeOfBirth);
        def.put("rdaGr2PlaceOfDeath"             , RDAGR2.placeOfDeath);
        def.put("rdaGr2ProfessionOrOccupation"   , RDAGR2.professionOrOccupation);
        def.put("owlSameAs"                      , OWL.sameAs);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("AgentImpl", def);
        _definitions.put("eu.europeana.corelib.solr.entity.AgentImpl", def);

        def = new MongoClassDef(EDM.Place);
        def.put("prefLabel"                      , SKOS.prefLabel);
        def.put("altLabel"                       , SKOS.altLabel);
        def.put("hasPart"                        , DCTerms.hasPart);
        def.put("isPartOf"                       , DCTerms.isPartOf);
        def.put("note"                           , SKOS.note);
        def.put("isNextInSequence"               , EDM.isNextInSequence);
        def.put("latitude"                       , WGS84.latitude);
        def.put("longitude"                      , WGS84.longitude);
        //def.put("lat"                            , WGS84.latitude);
        //def.put("long"                           , WGS84.longitude);
        //def.put("alt"                            , WGS84.altitude);
        def.put("owlSameAs"                      , OWL.sameAs);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("PlaceImpl", def);
        _definitions.put("eu.europeana.corelib.solr.entity.PlaceImpl", def);

        def = new MongoClassDef(EDM.TimeSpan);
        def.put("prefLabel"                      , SKOS.prefLabel);
        def.put("altLabel"                       , SKOS.altLabel);
        def.put("note"                           , SKOS.note);
        def.put("begin"                          , EDM.begin);
        def.put("end"                            , EDM.end);
        def.put("hasPart"                        , DCTerms.hasPart);
        def.put("isPartOf"                       , DCTerms.isPartOf);
        def.put("isNextInSequence"               , EDM.isNextInSequence);
        def.put("owlSameAs"                      , OWL.sameAs);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("TimespanImpl", def);
        _definitions.put("eu.europeana.corelib.solr.entity.TimespanImpl", def);

        def = new MongoClassDef(SKOS.Concept);
        def.put("prefLabel"                      , SKOS.prefLabel);
        def.put("altLabel"                       , SKOS.altLabel);
        def.put("note"                           , SKOS.note);
        def.put("broader"                        , SKOS.broader);
        def.put("narrower"                       , SKOS.narrower);
        def.put("related"                        , SKOS.related);
        def.put("broadMatch"                     , SKOS.broadMatch);
        def.put("narrowMatch"                    , SKOS.narrowMatch);
        def.put("relatedMatch"                   , SKOS.relatedMatch);
        def.put("exactMatch"                     , SKOS.exactMatch);
        def.put("closeMatch"                     , SKOS.closeMatch);
        def.put("notation"                       , SKOS.notation);
        def.put("owlSameAs"                      , OWL.sameAs);
        def.setPrefixes(EDM.PREFIXES);
        _definitions.put("ConceptImpl", def);
        _definitions.put("eu.europeana.corelib.solr.entity.ConceptImpl", def);
    }

    public static MongoClassDef getDefinition(String name)
    {
        return _definitions.get(name);
    }

    public static RDFDatatype getDatatype(Property p)
    {
        return _propDatatypes.get(p);
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
}
