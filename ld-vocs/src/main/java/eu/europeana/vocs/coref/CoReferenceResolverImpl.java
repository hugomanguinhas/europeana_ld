package eu.europeana.vocs.coref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class CoReferenceResolverImpl implements CoReferenceResolver
{
    protected static String   QUERY
        = "SELECT ?x WHERE { <#URI#> <http://www.w3.org/2002/07/owl#sameAs> ?x }";

    private String  _sparql;
    private Pattern _accept;

    public CoReferenceResolverImpl(String sparql) { this(sparql, null); }

    public CoReferenceResolverImpl(String sparql, Pattern accept)
    {
        _sparql = sparql;
        _accept = accept;
    }


    public void resolve(Map<String,String[]> uris)
    {
        for ( Map.Entry<String, String[]> entry : uris.entrySet() )
        {
            entry.setValue(resolve(entry.getKey()));
        }
    }

    public String[] resolve(String uri)
    {
        return execQuery(buildQuery(getTemplate(), uri));
    }

    private String[] execQuery(String query)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(_sparql, query);
        try {
            System.err.println(query);
            //endpoint.setSelectContentType(WebContent.contentTypeResultsXML);
            ResultSet rs = endpoint.execSelect();
            if ( !rs.hasNext() ) { return EMPTY; }

            List<String> l = new ArrayList<String>();
            while (rs.hasNext())
            {
                String sameAs = rs.next().getResource("x").getURI();
                if ( matches(sameAs) ) { l.add(sameAs); }
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

    protected boolean matches(String uri)
    {
        return (_accept == null ? true : _accept.matcher(uri).matches());
    }

    protected String getTemplate() { return QUERY; }

    protected String buildQuery(String template, String uri)
    {
        return template.replace("#URI#", uri);
    }
}
