/**
 * 
 */
package eu.europeana.ld.edm.mongo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.io.EDMDatasetFileNaming;
import eu.europeana.ld.edm.io.TurtleRecordWriter;
import eu.europeana.ld.edm.io.XMLRecordWriter;
import eu.europeana.ld.harvester.StoreCallback;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.store.LDStore;
import eu.europeana.ld.store.ZipLDStore;

import org.apache.jena.riot.EDMLang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class RunDatabaseDump
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient     cli = new MongoClient("144.76.218.178", 27017);
//      MongoClient     cli = new MongoClient("mongo2.eanadev.org", 27017);
        MongoDatabase   db  = cli.getDatabase("europeana");

        TurtleRecordWriter writer = new TurtleRecordWriter();
        writer.init(new FileOutputStream("D:/work/data/virtuoso/dump.ttl.gz"));

        ResourceCallback cb = new ResourceCallback() {

            @Override
            public void handle(Resource r, Status s)
            {
                try     { writer.write(r);                  }
                catch(IOException e) { e.printStackTrace(); }
                finally { r.getModel().removeAll();         }
            }
        };

        MongoEDMHarvester harvester = new MongoEDMHarvester(cli, db, null, false);
        try {
            harvester.harvestBySearch("{'about': { $regex: '^/2063602/.*' }}", cb);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { cli.close(); writer.close(); }
    }
}
