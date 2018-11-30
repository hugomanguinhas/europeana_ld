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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEDMHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2018
 */
public class TestMigrationOAI
{
    protected HttpClient    _client = new HttpClient();
    protected List<ServerAddress> seeds
        = Arrays.asList(new ServerAddress("mongo1-metis-prod.eanadev.org" ));
    protected List<MongoCredential> credentials
        = Arrays.asList(
        MongoCredential.createMongoCRCredential(
            "metis_production",
            "admin",
            "aHLRRgVPoyJn6l".toCharArray()
        )
    );
    //mongodb://example1.com,example2.com,example3.com/?replicaSet=test&readPreference=secondary
    protected MongoClient   _cli    = new MongoClient(seeds, credentials);
    protected MongoDatabase _db     = _cli.getDatabase("metis_publish_production");

    public TestMigrationOAI()
    {
        
    }

    public void validate(String recordId, File src, File dst, File comp) 
           throws Exception
    {
        Model m1   = getFromOAI(recordId);
        JenaUtils.store(m1, src);
        Model m2   = getFromMongo(recordId);
        JenaUtils.store(m2, dst);

        System.out.print("[" + m1.size());
        System.out.print("," + m2.size());
        System.out.println("," + m2.containsAll(m1));

        Model d1 = m1.difference(m2);
        Model d2 = m2.difference(m1);
        Model d3 = d1.union(d2);
        d3.setNsPrefixes(m2.getNsPrefixMap());
        JenaUtils.store(d3, comp);
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

            return loadModel(model, m);
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

    private Model loadModel(Model model, GetMethod m) throws IOException
    {
        String str = m.getResponseBodyAsString();
        int s = str.indexOf("<metadata>");
        int e = str.lastIndexOf("</metadata>");
        if ( s < 0 || e < 0 || s >= e ) { return model; }

        String xml = str.substring(s + 10, e);
        RDFDataMgr.read(model, new StringReader(xml), "", Lang.RDFXML);
        return model;
    }

    private String getURL(String recordId)
    {
        return "http://oai-pmh-test.eanadev.org/oai?verb=GetRecord"
             + "&metadataPrefix=edm"
             + "&identifier=http://data.europeana.eu/item" + recordId;
    }

    public static final void main(String[] args) throws Exception
    {
        File dir = new File("D:\\work\\incoming\\migration\\new\\");
        new TestMigrationOAI().validate("/9200506/https___handrit_is_rdf_cho_JS04_0259"
                             , new File(dir, "record_oai.xml")
                             , new File(dir, "record_mongo.xml")
                             , new File(dir, "record_diff.xml"));
    }
}
