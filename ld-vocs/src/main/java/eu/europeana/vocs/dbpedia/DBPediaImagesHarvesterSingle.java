/**
 * 
 */
package eu.europeana.vocs.dbpedia;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RiotException;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.vocs.VocsUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 23 Jun 2016
 */
public class DBPediaImagesHarvesterSingle
{
    private String _query = "SELECT ?image WHERE { ?obj foaf:depiction ?image }";

    public DBPediaImagesHarvesterSingle() {}

    public void harvest(Collection<String> uris, File file) throws IOException
    {
        Map<String,String> map = new HashMap();

        if ( file.exists() ) { load(file, map); }

        int size = uris.size();
        System.out.println("Harvesting images for [" + size + "] resources...");

        int i = 0;
        CSVPrinter p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
        try {
            for ( String uri : map.keySet() )
            {
                p.printRecord(uri, map.get(uri));
                ++i;
            }
            for ( String uri : uris )
            {
                if ( map.containsKey(uri) ) { continue; }

                p.printRecord(uri, fetchImage(uri));
                System.out.println("Harvested " + (++i) + " of " + size
                                 + " for resource: " + uri);
            }
            p.flush();
        }
        finally { IOUtils.closeQuietly(p); }
    }

    private static void load(File file, Map<String,String> map) 
            throws IOException
    {
        CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.EXCEL);
        try {
            for ( CSVRecord record : parser)
            {
                if ( record.size() <= 1 ) { continue; }

                String key   = record.get(0).trim();
                String value = record.get(1).trim();
                if ( key.isEmpty() || value.isEmpty() ) { continue; }

                map.put(key, value);
            }
        }
        finally { parser.close(); }
    }

    private String fetchImage(String uri)
    {
        String query = getQuery(uri);
        QueryEngineHTTP endpoint
            = new QueryEngineHTTP(VocsUtils.SPARQL_DBPEDIA_EN, query);
        try {
            ResultSet set = endpoint.execSelect();
            while ( set.hasNext() )
            {
                QuerySolution sol = set.next();
                String url = getImageURL(sol.get("image"));
                if ( url != null ) { return url; }
            }
        }
        catch (RiotException e) { System.out.println("Error: " + e.getMessage()); }
        finally                 { endpoint.close(); }
        return "";
    }

    private String getQuery(String uri)
    {
        return _query.replace("?obj", "<" + uri + ">");
    }

    private String getImageURL(RDFNode node)
    {
        String value = node.isLiteral() ? node.asLiteral().getString()
                                        : node.asResource().getURI();
        if ( value.startsWith("http://commons.wikimedia.org/") ) { return value; }
        if ( value.endsWith(".jpg") ) { return value; }
        return null;
    }

    private static String loadQuery() throws IOException
    {
        File file = new File("D:\\work\\git\\Europeana\\ld\\ld-vocs\\src\\main\\java\\eu\\europeana\\vocs\\dbpedia\\query2.sparql");
        return FileUtils.readFileToString(file);
        //FileUtils.readFileToString(file)DBPediaImagesHarvester.class.getResourceAsStream("query.sparql");
    }

    
}
