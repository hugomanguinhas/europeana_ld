/**
 * 
 */
package eu.europeana.vocs.isni;

import static eu.europeana.vocs.VocsUtils.SPARQL_WIKIDATA;
import static org.apache.http.HttpHeaders.ACCEPT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.util.FileUtils;

import eu.europeana.vocs.coref.CoReferenceResolver;
import eu.europeana.vocs.coref.CoReferenceUtils;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver;
import eu.europeana.vocs.wikidata.WikidataCoReferenceResolver.LiteralProcessor;

import static eu.europeana.vocs.isni.ISNIUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Mar 2018
 */
public class ISNI2Org
{
    private static String SPARQL = "https://query.wikidata.org/sparql";
    private static String ENTITY_RESOLVE = "http://entity.europeana.eu/entity/resolve?wskey=apidemo&uri=[URI]";
    private static String QUERY  = readQuery();

    private static CoReferenceResolver WD_2_GN
        = new WikidataCoReferenceResolver(
                SPARQL_WIKIDATA
              , "http://www.wikidata.org/prop/direct/P1566"
              , new LiteralProcessor("http://sws.geonames.org/#VALUE#/"));

    private static HttpClient CLIENT = new HttpClient();

    public void process(File src, File dst) throws IOException
    {
        CSVParser  parser  = new CSVParser(new FileReader(src), CSVFormat.EXCEL);
        CSVPrinter printer = new CSVPrinter(new FileWriter(dst), CSVFormat.EXCEL);

        try
        {
            for ( CSVRecord record : parser)
            {
                String dp   = record.get(0);
                String isni = record.get(1).trim();
                if ( !isISNI(isni) ) { System.err.println("skipped: " + isni); continue; }

                String wkd = getWikidata(isni);
                String gn  = getGeonames(wkd);
                String eid = getEntity(wkd);
                if ( eid == null ) { eid = getEntity(gn); }
                printer.printRecord(dp, toURI(isni), wkd, gn, eid);
            }
            printer.flush();
        }
        finally { parser.close(); printer.close(); }
    }

    private String getWikidata(String isni)
    {
        QueryEngineHTTP endpoint = new QueryEngineHTTP(SPARQL, getQuery(isni));
        try
        {
            ResultSet rs = endpoint.execSelect();
            while (rs.hasNext())
            {
                String uri = rs.next().getResource("obj").getURI();
                if ( rs.hasNext() ) { System.err.println("duplicates [" + isni + "]"); }
                return uri;
            }
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally             { endpoint.close();    }

        return null;
    }

    private String getGeonames(String wkd)
    {
        if ( wkd == null ) { return null; }

        for ( String gn : WD_2_GN.resolve(wkd) )
        {
            return gn;
        }
        return null;
    }

    private String getEntity(String wkd)
    {
        if ( wkd == null ) { return null; }

        String url = ENTITY_RESOLVE.replace("[URI]", wkd);
        System.out.println(url);
        GetMethod method = new GetMethod(url);
        method.setFollowRedirects(false);

        try {
            int iRet = CLIENT.executeMethod(method);

            if ( iRet == 200 ) { print(method); return null; }
            if ( iRet != 301 ) { return null; }

            Header header = method.getResponseHeader("Location");
            if ( header == null ) { return null; }

            return header.getValue();
        }
        catch (Exception e) { e.printStackTrace();        }
        finally             { method.releaseConnection(); }

        return null;
    }

    private void print(GetMethod method) throws IOException
    {
        System.err.println(method.getResponseBodyAsString());
    }

    public static final void main(String[] args) throws IOException
    {
        File src = new File("D:\\private\\organizations\\isni.csv");
        File dst = new File("D:\\private\\organizations\\isni_coref.csv");
        new ISNI2Org().process(src, dst);
    }
}
