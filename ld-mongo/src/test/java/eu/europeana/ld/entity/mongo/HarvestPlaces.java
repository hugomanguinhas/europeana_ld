/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.harvester.StoreCallback;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.mongo.callback.FileCallback;
import eu.europeana.ld.store.ZipLDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestPlaces
{

    public static final void main(String[] args) throws IOException
    {
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");
        

        File dstZip = new File("D:/work/data/entities/places.zip");
        StoreCallback cb = new StoreCallback(new ZipLDStore(dstZip, Lang.TTL));
        cb.begin();
        try { new MongoEntityHarvester(db, EDM.Place, true).harvestAll(cb); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); cb.finish(); }

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }
}
