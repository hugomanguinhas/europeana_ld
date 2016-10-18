/**
 * 
 */
package eu.europeana.ld.edm.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.io.EDMDataFileNaming;
import eu.europeana.ld.edm.io.EDMXMLWriter;
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
        MongoClient   client = new MongoClient("144.76.218.178", 27017);
        MongoDatabase db     = client.getDatabase("europeana");

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

        MongoEDMHarvester harvester = new MongoEDMHarvester(db, null, true);
        File dstZip = new File("D:/work/data/mongo/dataset.zip");
        LDStore store = new ZipLDStore(dstZip, new EDMXMLWriter()
                      , Lang.RDFXML, new EDMDataFileNaming());
        StoreCallback cb = new StoreCallback(store);
        cb.begin();
        try { 
            //harvester.harvest("http://data.europeana.eu/item/2063602/SWE_280_001", cb);
            harvester.harvestBySearch("{'about': { $regex: '^/2063602/.*' }}", cb);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); cb.finish(); }

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }
}
