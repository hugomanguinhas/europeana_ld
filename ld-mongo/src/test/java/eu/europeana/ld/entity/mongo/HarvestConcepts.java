/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.SKOS;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.mongo.callback.FileCallback;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestConcepts
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");
        

        File dst = new File("D:/work/data/entities/concepts.xml");
        FileCallback cb = new FileCallback(dst);
        try { new MongoEntityHarvester(db, SKOS.Concept, true).harvestAll(cb); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }
        cb.finish();

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }
}
