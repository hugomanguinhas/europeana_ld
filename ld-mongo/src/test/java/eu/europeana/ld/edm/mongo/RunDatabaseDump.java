/**
 * 
 */
package eu.europeana.ld.edm.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.io.EDMDataFileNaming;
import eu.europeana.ld.edm.io.EDMTurtleWriter;
import eu.europeana.ld.edm.io.EDMXMLWriter;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.harvester.StoreCallback;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.store.LDStore;
import eu.europeana.ld.store.ZipLDStore;
import eu.europeana.mongo.callback.FileCallback;

import org.apache.jena.riot.EDMLang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class RunDatabaseDump
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient     client = new MongoClient("144.76.218.178", 27017);
//        MongoClient     client = new MongoClient("mongo2.eanadev.org", 27017);
        MongoDatabase   db     = client.getDatabase("europeana");

        EDMTurtleWriter writer = new EDMTurtleWriter();
        writer.start(new File("D:/work/data/virtuoso/dump.ttl.gz"));

        HarvesterCallback cb = new HarvesterCallback() {

            @Override
            public void handle(Resource r)
            {
                Model m = r.getModel();
                try     { writer.write(m); }
                catch(IOException e) { e.printStackTrace(); }
                finally { m.removeAll();   }
            }
        };

        MongoEDMHarvester harvester = new MongoEDMHarvester(db, null);
        try {
            harvester.harvestBySearch("{'about': { $regex: '^/2063602/.*' }}", cb);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); writer.finish(); }
    }
}
