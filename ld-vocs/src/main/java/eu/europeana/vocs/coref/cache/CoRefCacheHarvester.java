package eu.europeana.vocs.coref.cache;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class CoRefCacheHarvester
{
    private String  _sparql;

    public CoRefCacheHarvester(String  sparql)
    {
        _sparql = sparql;
    }
    
    public void harvest(String query, File fileCache)
    {
        CSVPrinter p = null;
        try {
            p = new CSVPrinter(new PrintStream(fileCache), CSVFormat.EXCEL);
            harvest(query, p);
            p.flush();
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(p); }
    }

    private void harvest(String query, CSVPrinter p)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(_sparql, query);
        try {
            System.err.println(query);
            ResultSet rs = endpoint.execSelect();
            if ( !rs.hasNext() ) { return; }

            int cursor = 0;
            while (rs.hasNext())
            {
                QuerySolution sol = rs.next();
                String src = sol.getResource("src").getURI();
                String trg = sol.getResource("trg").getURI();
                p.printRecord(src, trg);

                if (++cursor % 1000 == 0) { System.out.println("fetched: " + cursor); }
            }
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { endpoint.close(); }
    }

    public static final void main(String[] args)
    {
        String query = "PREFIX tgsi: <http://data.tagasauris.com/ontologies/core/> "
                     + "SELECT ?src ?trg { GRAPH <urn:c5:matches> "
                       + "{ ?src tgsi:exactMatch ?trg . FILTER strstarts(str(?trg), \"http://dbpedia.org/resource/\") } "
                     + "}";
        File cache = new File("D:\\work\\incoming\\taskforce\\cache\\onto.coref.cache.csv");

        new CoRefCacheHarvester("http://mediagraph.ontotext.com/repositories/c5").harvest(query, cache);
    }
}
