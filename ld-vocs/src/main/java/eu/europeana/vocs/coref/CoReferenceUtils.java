package eu.europeana.vocs.coref;

import static eu.europeana.vocs.VocsUtils.*;

import java.util.regex.Pattern;

import eu.europeana.vocs.VocsUtils;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver.LiteralProcessor;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver.ResourceProcessor;
import eu.europeana.vocs.wikidata.GN2WKDCoReferencer;

public class CoReferenceUtils
{
    public static CoReferenceResolver GN_2_DBP
        = new CoReferenceResolverInv(VocsUtils.SPARQL_DBPEDIA_EN);

    public static CoReferenceResolver GN_2_WD
        = new GN2WKDCoReferencer(
            SPARQL_WIKIDATA
          , "http://www.wikidata.org/prop/direct/P1566");

    public static CoReferenceResolver WD_2_FB
        = new WikidataCoReferenceResolver(
                SPARQL_WIKIDATA
              , "http://www.wikidata.org/entity/P646-freebase"
              , new ResourceProcessor());

    public static CoReferenceResolver WD_2_GN
        = new WikidataCoReferenceResolver(
                SPARQL_WIKIDATA
              , "http://www.wikidata.org/prop/direct/P1566"
              , new LiteralProcessor("http://sws.geonames.org/#VALUE#/"));

    public static CoReferenceResolver WD_2_DBP
        = new CoReferenceResolverInv(
                SPARQL_DBPEDIA_EN
              , Pattern.compile("http://dbpedia[.]org.*"));

    public static CoReferenceResolver FB_2_DBP
        = new CoReferenceResolverInv(
                SPARQL_DBPEDIA_EN
              , Pattern.compile("http://dbpedia[.]org.*"));

    public static CoReferenceResolver WD_2_FB_2_DBP
        = new CoReferenceResolverChain(WD_2_FB, FB_2_DBP);


    public static CoReferenceResolver ONTO_2_DBP
        = new CoReferenceResolverOnto(
            "http://mediagraph.ontotext.com/repositories/c5"
          , Pattern.compile("http://dbpedia[.]org.*"));
}
