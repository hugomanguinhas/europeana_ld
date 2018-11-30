/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.ResourceCallback.Status;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.io.XMLIterativeResourceWriter;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestConcepts
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient   cli = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db  = cli.getDatabase("annocultor_db");
        

        File          file = new File("D:/work/data/entities/concepts/concepts.xml");
        store2XML(cli, db, file);
    }

    private static void store2XML(MongoClient cli, MongoDatabase db, File file) 
            throws IOException
    {
        XMLIterativeResourceWriter writer = new XMLIterativeResourceWriter();
        writer.init(new FileOutputStream(file));

        ResourceCallback cb = new ResourceCallback<Resource>() {

            @Override
            public void handle(String id, Resource r, Status s)
            {
                try                   { writer.write(r);               }
                catch (IOException e) { throw new RuntimeException(e); }
                finally               { r.getModel().removeAll();      }
            }
        };

        MongoEntityHarvester h = new MongoEntityHarvester(cli, db, SKOS.Concept);
        try                 { h.harvestAll(cb);                }
        catch (Throwable t) { t.printStackTrace();             }
        finally             { closeQuietly(writer); h.close(); cli.close(); }
    }
}
