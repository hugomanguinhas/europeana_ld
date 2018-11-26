package eu.europeana.vocs.wikidata;

import static eu.europeana.vocs.coref.CoReferenceUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.vocs.coref.CoReferenceResolver;
import eu.europeana.vocs.gn.GNUtils;

public class GN2WKDCoReferencer implements CoReferenceResolver
{
    private static String   QUERY = "SELECT ?x WHERE { ?x <#PROPERTY#> '#URI#' }";
    private static String[] EMPTY = new String[] {};

    private String    _sparql  = null;
    private String    _prop    = null;

    public GN2WKDCoReferencer(String sparql, String prop)
    {
        _prop      = prop;
        _sparql    = sparql;
    }

    public String[] resolve(String uri)
    {
        return execQuery(buildQuery(QUERY, _prop, uri));
    }

    public void resolve(Map<String,String[]> uris)
    {
        for ( Map.Entry<String, String[]> entry : uris.entrySet() )
        {
            entry.setValue(resolve(entry.getKey()));
        }
    }

    private String[] execQuery(String query)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(_sparql, query);
        try {
            ResultSet rs = endpoint.execSelect();
            if ( !rs.hasNext() ) { return EMPTY; }

            List<String> l = new ArrayList<String>();
            while (rs.hasNext())
            {
                String uri = rs.next().getResource("x").getURI();
                if ( uri != null ) { l.add(uri); }
            }
            return ( l.size() == 0 ? EMPTY : l.toArray(EMPTY) );
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            endpoint.close();
        }
        return EMPTY;
    }

    private String buildQuery(String template, String prop, String uri)
    {
        String id = GNUtils.getId(uri);
        if ( id == null ) { return null; }

        return template.replace("#PROPERTY#", prop).replace("#URI#", id);
    }

    public static interface Processor
    {
        public String process(RDFNode node);
    }

    public static class ResourceProcessor implements Processor
    {
        public String process(RDFNode node)
        {
            return node.asResource().getURI();
        }
    }

    public static final void main(String[] args)
    {
        String[] res = GN_2_WD.resolve("http://sws.geonames.org/2911288/");
        System.out.println(Arrays.asList(res).toString());
    }
}
