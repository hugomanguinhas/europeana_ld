/**
 * 
 */
package eu.europeana.vocs.isni;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Mar 2018
 */
public class WikidataGLAMs2CSV
{
    private static String SPARQL = "https://query.wikidata.org/sparql";
    private static String QUERY  = "SELECT ?lbl WHERE { <#URI#> rdfs:label ?lbl }";

    public void generate(File src, File dst) throws IOException
    {
        CSVParser  parser  = new CSVParser(new FileReader(src), CSVFormat.EXCEL);
        CSVPrinter printer = new CSVPrinter(new FileWriter(dst), CSVFormat.EXCEL);

        try
        {
            for ( CSVRecord record : parser)
            {
                String wkd  = record.get(0).trim();
                String isni = record.get(1).trim();

                printer.printRecord(wkd, isni, getLabel(wkd));
            }
            printer.flush();
        }
        finally { parser.close(); printer.close(); }
    }

    private String getLabelsQuery(String wkd)
    {
        return QUERY.replace("#URI#", wkd);
    }

    private String getLabel(String wkd)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap();
        QueryEngineHTTP endpoint = new QueryEngineHTTP(SPARQL
                                                     , getLabelsQuery(wkd));
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

    public static final void main(String[] args) throws IOException
    {
        File src = new File("D:\\work\\incoming\\organizations\\wikidata_glams.csv");
        File dst = new File("D:\\work\\incoming\\organizations\\wikidata_glams_labels.csv");
        new WikidataGLAMs2CSV().generate(src, dst);
    }
}
