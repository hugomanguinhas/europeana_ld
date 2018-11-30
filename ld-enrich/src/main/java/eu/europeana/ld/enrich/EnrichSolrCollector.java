/**
 * 
 */
package eu.europeana.ld.enrich;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.jayway.jsonpath.JsonPath;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 8 Aug 2017
 */
public class EnrichSolrCollector
{
    private static Logger _log = Logger.getLogger(EnrichSolrCollector.class);

    private static int DEFAULT_PART_SIZE = 10000;
    private static String FIELD  = "FIELD";
    private static String LIMIT  = "LIMIT";
    private static String OFFSET = "OFFSET";

    private String _endpoint;
    private int    _limit    = Integer.MAX_VALUE;
    private int    _partSize = DEFAULT_PART_SIZE;

    private String              _queryFacet;
    private CloseableHttpClient _client;

    public EnrichSolrCollector(String endpoint)
    {
        this(endpoint, "http://data.europeana.eu/"
           , DEFAULT_PART_SIZE, Integer.MAX_VALUE);
    }

    public EnrichSolrCollector(String endpoint, String prefix
                             , int partSize, int limit)
    {
        _client   = HttpClientBuilder.create().build();
        _endpoint = endpoint;
        _queryFacet = _endpoint
                    + "?q=*:*&wt=json&rows=0&facet=true"
                    + "&facet.mincount=1&facet.sort=index"
                    + "&facet.prefix=" + prefix
                    + "&facet.field=FIELD&facet.limit=LIMIT&facet.offset=OFFSET";
        _partSize = partSize;
        _limit    = limit;
    }

    public String getFacetQuery(String facet)
    {
        return getQueryURL(facet, 0, _partSize);
    }

    public void get(String facet, Map<String,Integer> map)
    {
        FacetRetrievalHandler hdl = new FacetRetrievalHandler(map);
        int offset = 0;
        while ( true )
        {
            int count = get(facet, offset, hdl);
            if ( count == 0 ) { break; }
            offset += count;
        }
    }

    public void get(String facet, PrintStream ps)
    {
        CSVPrinter p = null;
        try
        {
            p = new CSVPrinter(ps, CSVFormat.EXCEL);
            Map<String,Integer>   map = new LinkedHashMap();
            FacetRetrievalHandler hdl = new FacetRetrievalHandler(map);
            int offset = 0;
            while ( true )
            {
                int count = get(facet, offset, hdl);
                if ( count == 0 ) { break; }
                print(map, p);
                map.clear();
                offset += count;
                p.flush();
            }
        }
        catch (IOException e) {}
        finally               { IOUtils.closeQuietly(p); }
    }

    private void print(Map<String,Integer> map, CSVPrinter p)
            throws IOException
    {
        for ( Map.Entry<String, Integer> entry : map.entrySet())
        {
            p.printRecord(entry.getKey(), entry.getValue());
        }
    }

    private int get(String facet, int offset, FacetRetrievalHandler hdl)
    {
        int    limit = Math.min(_partSize, _limit - offset);
        return queryAPI(getQueryURL(facet, offset, limit), hdl);
    }

    private String getQueryURL(String facet, int offset, int limit)
    {
        return _queryFacet.replace(FIELD , facet)
                          .replace(LIMIT , String.valueOf(limit))
                          .replace(OFFSET, String.valueOf(offset));
    }

    private int queryAPI(String query, FacetRetrievalHandler handler)
    {
        System.out.println(query);
        HttpGet m = new HttpGet(query);
        try                    { return _client.execute(m, handler); }
        catch (IOException e ) { e.printStackTrace(); return 0;      }
        finally                { m.releaseConnection();              }
    }

    private class FacetRetrievalHandler
            implements ResponseHandler<Integer>
    {
        private Map<String,Integer> _results;

        public FacetRetrievalHandler(Map<String,Integer> res){ _results = res; }

        public Integer handleResponse(HttpResponse rsp)
               throws ClientProtocolException, IOException
        {
            int size = 0;

            int code = rsp.getStatusLine().getStatusCode();
            if ( code != 200 ) { System.out.println("HTTP error: " + code); return size; }

            InputStream is = null;
            try {
                is = getInputStream(rsp);
                List values = (List)JsonPath.read(is, "$.facet_counts.facet_fields[*][*]");

                int len = values.size();
                for ( int i = 0; i < len; i += 2 )
                {
                    String facet = (String)values.get(i);
                    int    count = (Integer)values.get(i+1);
                    _results.put(facet, count);
                    size++;
                }
            }
            catch (IOException e)  { e.printStackTrace();      }
            finally                { IOUtils.closeQuietly(is); }

            return size;
        }

        private InputStream getInputStream(HttpResponse rsp)
                throws IOException
        {
            return rsp.getEntity().getContent();
        }
    }

    public static void main( String[] args ) throws FileNotFoundException
    {
        PrintStream ps = new PrintStream("D:\\work\\data\\entities\\timespans\\enrich\\edm_timespan.facet.csv");
        new EnrichSolrCollector(
            "http://sol13.eanadev.org:9191/solr/search_production_publish_1_shard1_replica2/search"
          , "http://semium.org/", DEFAULT_PART_SIZE, Integer.MAX_VALUE)
            .get("edm_timespan", ps);
    }
}
