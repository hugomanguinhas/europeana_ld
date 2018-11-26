/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.util.LinkedHashMap;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 16 Mar 2018
 */
public class WikidataUtils
{
    private static String QUERY  = "SELECT ?lbl WHERE { <#URI#> rdfs:label ?lbl }";

    public static String getLabel(String wkd, String sparql)
    {
        if ( wkd.startsWith("http://www.wikidata.org/prop/") )
        {
            wkd = wkd.replace("http://www.wikidata.org/prop/"
                            , "http://www.wikidata.org/entity/");
        }

        String query = QUERY.replace("#URI#", wkd);
        LinkedHashMap<String,String> map = new LinkedHashMap();
        QueryEngineHTTP endpoint = new QueryEngineHTTP(sparql, query);
        try
        {
            ResultSet rs = endpoint.execSelect();
            while (rs.hasNext())
            {
                Literal label = rs.next().getLiteral("lbl");
                if ( map.containsKey(label.getLanguage()) ) { continue; }

                map.put(label.getLanguage(), label.getString());
            }

            if ( map.isEmpty()         ) { return null;          }
            if ( map.containsKey("en") ) { return map.get("en"); }

            return map.get(map.keySet().iterator().next());
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally             { endpoint.close();    }

        return null;
    }

}
