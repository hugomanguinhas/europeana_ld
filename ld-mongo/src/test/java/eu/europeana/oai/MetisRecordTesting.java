/**
 * 
 */
package eu.europeana.oai;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEDMHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2018
 */
public class MetisRecordTesting
{
    protected HttpClient    _client = new HttpClient();
    protected String        _api = "https://metis-publish-api-prod.eanadev.org/api/v2/record";
    protected MongoClient   _cli    = new MongoClient(new MongoClientURI("mongodb://metis_production:aHLRRgVPoyJn6l@mongo1-metis-prod.eanadev.org/admin"));
    protected MongoDatabase _db     = _cli.getDatabase("metis-publish-production");

    public MetisRecordTesting() {}

    public void validate(String recordId
                       , File fOAI, File fMongo, File fAPI) 
           throws Exception
    {
        JenaUtils.store(getFromOAI(recordId)  , fOAI);
        JenaUtils.store(getFromMongo(recordId), fMongo);
        //JenaUtils.store(getFromAPI(recordId)  , fAPI);
    }

    public void compare(File f1, File f2, File comp) 
           throws Exception
    {
        Model m1 = JenaUtils.load(f1);
        Model m2 = JenaUtils.load(f2);

        System.out.print("[" + m1.size());
        System.out.print("," + m2.size());
        System.out.println("," + m2.containsAll(m1));

        Model d1 = m1.difference(m2);
        Model d2 = m2.difference(m1);
        Model d3 = d1.union(d2);
        EDM.setPrefixes(d3);
        JenaUtils.store(d3, comp);
    }

    private String getBaseURL(String id)
    {
        String ds  = id.substring(0, id.indexOf('/', 1));
        return ( "http://data.europeana.eu/item" + ds );
    }

    private Model getFromMongo(String recordId)
    {
        MongoEDMHarvester harvester = new MongoEDMHarvester(_cli, _db, null, false);
        return harvester.harvest(recordId).getModel();
    }

    private Model getFromOAI(String recordId) throws HttpException, IOException
    {
        Model model = ModelFactory.createDefaultModel();
        String url = getURL(recordId);
        System.out.println(url);
        GetMethod m = new GetMethod(url);
        try {
            int r = _client.executeMethod(m);
            if ( r != 200 ) { return model; }

            return loadModel(model, m.getResponseBodyAsString()
                           , getBaseURL(recordId));
        }
        finally { closeMethod(m); }
    }

    private Model getFromAPI(String recordId) throws HttpException, IOException
    {
        Model model = ModelFactory.createDefaultModel();
        String url = getAPIURL(recordId);
        System.out.println(url);

        try
        {
            HttpURLConnection conn = 
                (HttpURLConnection)new URL(url).openConnection();

            int code = conn.getResponseCode();
            if ( code != 200 ) { System.err.println("ERROR: " + code); return model; }
            
            String str = IOUtils.toString(conn.getInputStream());
            return loadModel(model, str, getBaseURL(recordId));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return model;
    }

    private Model getFrom2API(String recordId) throws HttpException, IOException
    {
        Model model = ModelFactory.createDefaultModel();
        String url = getAPIURL(recordId);
        System.out.println(url);

        GetMethod m = new GetMethod(url);
        try {
            int r = _client.executeMethod(m);
            if ( r != 200 ) { System.err.println("ERROR: " + r); return model; }

            return loadModel(model, m.getResponseBodyAsString(), getBaseURL(recordId));
        }
        finally { closeMethod(m); }
    }

    protected void closeMethod(HttpMethod m)
    {
        try {
            closeQuietly(m.getResponseBodyAsStream());
            m.releaseConnection();
        }
        catch (IOException e) {} 
    }

    private Model loadModel(Model model, String str
                          , String baseURL) throws IOException
    {
        int s = str.indexOf("<metadata>");
        int e = str.lastIndexOf("</metadata>");
        if ( s < 0 || e < 0 || s >= e ) { return model; }

        String xml = str.substring(s + 10, e);
        xml = xml.replace("<rdf:RDF ", "<rdf:RDF xml:base=\"" + baseURL + "\" ");

        RDFDataMgr.read(model, new StringReader(xml), "", Lang.RDFXML);
        return fixDataNamespace(model);
    }

    private Model fixDataNamespace(Model m)
    {
        for ( Resource r : m.listResourcesWithProperty(RDF.type, EDM.ProvidedCHO).toList() )
        {
            String uri = r.getURI();
            if ( uri.contains("/item/") ) { continue; }

            uri = uri.replace("http://data.europeana.eu/"
                            , "http://data.europeana.eu/item/");
            ResourceUtils.renameResource(r, uri);
        }
        return m;
    }

    private String getURL(String recordId)
    {
        return "http://oai-pmh.eanadev.org/oai?verb=GetRecord"
             + "&metadataPrefix=edm"
             + "&identifier=http://data.europeana.eu/item" + recordId;
    }

    private String getAPIURL(String recordId)
    {
        return _api + recordId + ".rdf?wskey=api2demo" ;
    }

    public static final void main(String[] args) throws Exception
    {
        File dir = new File("D:\\work\\incoming\\migration\\new\\");
        MetisRecordTesting metis = new MetisRecordTesting();
        File f1 = new File(dir, "record_oai.xml");
        File f2 = new File(dir, "record_mongo.xml");
        File f3 = new File(dir, "record_api.xml");
        metis.validate("/000006/UEDIN_214", f1, f2, f3);
        metis.compare(f1, f2, new File(dir, "record_mongo_diff.xml"));
        metis.compare(f1, f3, new File(dir, "record_api_diff.xml"));
    }
}
