/**
 * 
 */
package eu.europeana.vocs.isni;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Mar 2018
 */
public class ZohoOrgUtils
{
    public static String SPARQL = "https://query.wikidata.org/sparql";

    private static String WKD_LABELS_QUERY 
        = "SELECT ?lbl WHERE { <#URI#> rdfs:label ?lbl }";

    private static LevenshteinDistance _distance = new LevenshteinDistance();
    private static float               _threshold = 0.90f;

    public static String normalize(String str)
    {
        str = Normalizer.normalize(str.trim().replaceAll("\\s", " ")
                                 , Normalizer.Form.NFD);
        return str.replaceAll("\\p{M}", "").toLowerCase();
    }

    public static boolean isSimilar(String s1, String s2)
    {
        Integer i      = _distance.apply(s1, s2);
        int     len    = Math.max(s1.length(), s2.length());
        int     shared = len - i;
        return ( ((float)shared / len) >= _threshold );
    }

    public static Map<String,String> loadZohoOrgs(File src) throws IOException
    {
        CSVParser  parser  = new CSVParser(new FileReader(src), CSVFormat.EXCEL);

        Map<String,String> map = new HashMap();
        try
        {
            for ( CSVRecord record : parser)
            {
                String zohoID = record.get(0).trim();
                if ( zohoID.equals("ACCOUNTID") ) { continue; }

                for ( int i = 1; i < record.size(); i++)
                {
                    String label  = normalize(record.get(i));
                    if ( !label.isEmpty() ) { map.put(label, zohoID); }
                }
            }
            map.remove("true");
        }
        finally { parser.close(); }

        return map;
    }

    public static Set<String> getWkdLabelsNormalized(String wkd, Set<String> set)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(SPARQL
                                                     , getLabelsQuery(wkd));
        try
        {
            ResultSet rs = endpoint.execSelect();
            while (rs.hasNext())
            {
                Literal label = rs.next().getLiteral("lbl");
                set.add(normalize(label.getString()));
            }
            return set;
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally             { endpoint.close();    }

        return set;
    }

    private static String getLabelsQuery(String wkd)
    {
        return WKD_LABELS_QUERY.replace("#URI#", wkd);
    }
}
