/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.out.JsonLdWriter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.ResourceCallback.Status;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestAgents
{

    public static final void main(String[] args) throws IOException
    {
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");

        //D:\work\git\Europeana\ld\ld-entity\src\main\resources\etc\context\entity.jsonld
        URL url = new URL("file:///D:/work/git/Europeana/ld/ld-entity/src/main/resources/etc/context/entity.jsonld");
        new JsonLdWriter(url).write(m, new OutputStreamWriter(System.out));
        File dst = new File("D:/work/data/entities/agents.xml");

        ResourceCallback cb = new ResourceCallback() {

            @Override
            public void handle(String id, Object r, Status s)
            {
                
            }
        };
        try { new MongoEntityHarvester(client, db, EDM.Agent).harvestAll(cb); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }
        cb.finish();

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }
}
