/**
 * 
 */
package eu.europeana.ld.edm.mongo;

import java.io.IOException;

import org.apache.jena.rdf.model.Resource;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.io.XMLRecordWriter;
import eu.europeana.ld.mongo.MongoEDMHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestData
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient   cli = new MongoClient("mongo1.eanadev.org", 27017);
        MongoDatabase db  = cli.getDatabase("europeana_production_publish_1");

        //MongoClient   cli = new MongoClient("144.76.218.178", 27017);
        //MongoDatabase db  = cli.getDatabase("europeana");

        /*
        File dst = new File("D:/work/data/mongo/aggregation.xml");
        FileCallback cb = new FileCallback(dst);
        try { new MongoEDMHarvester(db, ORE.Aggregation, true).harvestAll(cb); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }
        cb.finish();
        */

        /*
        File dst = new File("D:/work/data/mongo/testRecord.xml");
        FileCallback cb = new FileCallback(dst);
        try { new MongoEDMHarvester(db, null, true).harvest("http://data.europeana.eu/item/2063602/SWE_280_001", cb); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }
        cb.finish();
        */

        XMLRecordWriter writer = new XMLRecordWriter();
        MongoEDMHarvester harvester = new MongoEDMHarvester(cli, db, null, false, false);

        ResourceCallback cb = new ResourceCallback<Resource>() {

            @Override
            public void handle(String uri, Resource r, Status s)
            {
                try { writer.write(r, System.out); }
                catch (IOException e) {}
            }

        };
        try {
            harvester.harvest("http://data.europeana.eu/item/000006/UEDIN_214", cb);
            //harvester.harvest("http://data.europeana.eu/item/2048329/providedCHO_SE_SSA_1491_C_I_1__", cb);
            //harvester.harvestBySearch("{'about': { $regex: '^/2063602/.*' }}", cb);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { cli.close(); }

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }
}
