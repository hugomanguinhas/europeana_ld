/**
 * 
 */
package eu.europeana.vocs.dbpedia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
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
public class DBPediaImagesHarvester
{
    private String _query;

    public DBPediaImagesHarvester() throws IOException
    {
        _query = loadQuery();
    }

    public void harvest(Collection<String> uris, File file) throws IOException
    {
        int size = uris.size();
        System.out.println("Harvesting images for [" + size + "] resources...");

        CSVPrinter p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
        QueryEngineHTTP endpoint
            = new QueryEngineHTTP(VocsUtils.SPARQL_DBPEDIA_EN, _query);
        try {
            ResultSet set = endpoint.execSelect();
            while ( set.hasNext() )
            {
                System.out.println(set.getRowNumber());
                QuerySolution sol = set.next();
                String uri = sol.getResource("obj").getURI();
                if ( !uris.contains(uri) ) { continue; }

                RDFNode node  = sol.get("image");
                String  value = node.isLiteral() ? node.asLiteral().getString()
                                                 : node.asResource().getURI();
                p.printRecord(uri,value);
            }
        }
        catch (RiotException e) {
            System.out.println("Error: " + e.getMessage());
        }
        finally {
            endpoint.close();
        }
    }

    private static String loadQuery() throws IOException
    {
        File file = new File("D:\\work\\git\\Europeana\\ld\\ld-vocs\\src\\main\\java\\eu\\europeana\\vocs\\dbpedia\\query.sparql");
        return FileUtils.readFileToString(file);
        //FileUtils.readFileToString(file)DBPediaImagesHarvester.class.getResourceAsStream("query.sparql");
    }

    
}
