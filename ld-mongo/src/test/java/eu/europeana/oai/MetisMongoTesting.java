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
public class MetisMongoTesting
{
    public MetisMongoTesting() {}

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

    public static final void main(String[] args) throws Exception
    {
        MongoClient   srcCLI = new MongoClient(new MongoClientURI("mongodb://metis_production:aHLRRgVPoyJn6l@mongo1-metis-prod.eanadev.org/admin"));
        MongoDatabase srcDB  = srcCLI.getDatabase("metis-preview-production");
        MongoClient   dstCLI = new MongoClient("mongo1.eanadev.org", 27017);
        MongoDatabase dstDB  = dstCLI.getDatabase("europeana_production_publish_1");

        MongoEDMHarvester h1 = new MongoEDMHarvester(srcCLI, srcDB, null, false, true);
        MongoEDMHarvester h2 = new MongoEDMHarvester(dstCLI, dstDB, null, false, true);
        
        String[] ds = new String[] { "2048441", "04802", "2022111", "9200506"
                                   , "15416", "9200505", "9200574" };

        File dir = new File("D:\\work\\incoming\\migration\\new\\");

        File f1 = new File(dir, "ds_prod.xml");
        Model m1 = h1.harvestDataset(ds);
        JenaUtils.store(m1, f1);

        File f2 = new File(dir, "ds_metis.xml");
        Model m2 = h2.harvestDataset(ds);
        JenaUtils.store(m2, f2);

        MetisMongoTesting metis = new MetisMongoTesting();
        metis.compare(f1, f2, new File(dir, "ds_diff.xml"));
    }
}
