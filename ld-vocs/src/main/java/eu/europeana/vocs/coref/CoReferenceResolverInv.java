package eu.europeana.vocs.coref;

import java.util.Arrays;
import java.util.regex.Pattern;

import eu.europeana.vocs.VocsUtils;

public class CoReferenceResolverInv extends CoReferenceResolverImpl
{
    private static String QUERY
        = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
        + "SELECT ?x WHERE { ?x owl:sameAs <#URI#> }";

    public CoReferenceResolverInv(String query) { super(query); }

    public CoReferenceResolverInv(String query, Pattern p) { super(query, p); }

    protected String getTemplate() { return QUERY; }

    public static void main(String[] args)
    {
        CoReferenceResolverInv cr = new CoReferenceResolverInv(
                VocsUtils.SPARQL_DBPEDIA_EN);
        System.out.println(Arrays.asList(cr.resolve("http://www.wikidata.org/entity/Q1065742")).toString());

        //CoReferenceResolverInv cr = new CoReferenceResolverInv("http://dbpedia.org/sparql", Pattern.compile("http://dbpedia[.]org.*"));
        //System.out.println(Arrays.asList(cr.resolve("http://wikidata.org/entity/Q134307")).toString());
    }
}
