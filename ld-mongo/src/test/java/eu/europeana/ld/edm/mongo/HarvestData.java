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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.io.EDMDataFileNaming;
import eu.europeana.ld.edm.io.EDMXMLWriter;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.harvester.StoreCallback;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.mongo.callback.FileCallback;
import eu.europeana.ld.store.LDStore;
import eu.europeana.ld.store.ZipLDStore;

import org.apache.jena.riot.EDMLang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestData
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient   cli = new MongoClient("144.76.218.178", 27017);
        MongoDatabase db  = cli.getDatabase("europeana");

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

        EDMXMLWriter writer = new EDMXMLWriter();
        MongoEDMHarvester harvester = new MongoEDMHarvester(cli, db, null);

        HarvesterCallback cb = new HarvesterCallback() {

            @Override
            public void handle(Resource r, Status s)
            {
                try { writer.write(r.getModel(), System.out); }
                catch (IOException e) {}
            }
        };
        try {
            harvester.harvest("http://data.europeana.eu/item/2048329/providedCHO_SE_SSA_0005E_A_I_a_1_27_", cb);
            //harvester.harvest("http://data.europeana.eu/item/2048329/providedCHO_SE_SSA_1491_C_I_1__", cb);
            //harvester.harvestBySearch("{'about': { $regex: '^/2063602/.*' }}", cb);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { cli.close(); }

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }
}
