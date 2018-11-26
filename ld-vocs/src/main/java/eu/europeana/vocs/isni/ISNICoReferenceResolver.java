/**
 * 
 */
package eu.europeana.vocs.isni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.vocs.coref.CoReferenceResolver;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Mar 2018
 */
public class ISNICoReferenceResolver implements CoReferenceResolver
{
    private static String QUERY
        = "SELECT ?obj WHERE { ?obj <http://www.wikidata.org/prop/direct/P213> \"#ID#\" }";

    private String _sparql;

    public ISNICoReferenceResolver(String sparql) { _sparql = sparql; }

    public String[] resolve(String isni)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(_sparql, getQuery(isni));
        try
        {
            List<String> l = new ArrayList<String>();
            ResultSet rs = endpoint.execSelect();
            while (rs.hasNext())
            {
                l.add(rs.next().getResource("obj").getURI());
            }

            return ( l.size() == 0 ? EMPTY : l.toArray(EMPTY) );
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally             { endpoint.close();    }

        return EMPTY;
    }

    private String getQuery(String isni)
    {
        return QUERY.replace("#ID#", isni);
    }

    public void resolve(Map<String,String[]> uris)
    {
        for ( Map.Entry<String, String[]> entry : uris.entrySet() )
        {
            entry.setValue(resolve(entry.getKey()));
        }
    }

    public static final void main(String[] args)
    {
        System.out.println(StringUtils.join(new ISNICoReferenceResolver("https://query.wikidata.org/sparql").resolve("0000 0001 2353 1945"), ","));
    }
}