/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.io.XMLIterativeResourceWriter;
import eu.europeana.ld.harvester.StoreCallback;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.store.ZipLDStore;

import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestPlaces
{

    public static final void main(String[] args) throws IOException
    {
        if (args.length < 1) { return; }
        File          file = new File(args[0]);

        file.getParentFile().mkdirs();

        MongoClient   cli  = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db   = cli.getDatabase("annocultor_db");
        //File          file = new File("D:/work/data/entities/places.xml");
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

        MongoEntityHarvester h = new MongoEntityHarvester(cli, db, EDM.Place);
        try                 { h.harvestAll(cb);                }
        catch (Throwable t) { t.printStackTrace();             }
        finally             { closeQuietly(writer); h.close(); cli.close(); }
    }

    private static void store2Zip(MongoClient cli, MongoDatabase db, File file)
    {
        StoreCallback cb = new StoreCallback(new ZipLDStore(file, Lang.TTL));
        cb.begin();

        MongoEntityHarvester h = new MongoEntityHarvester(cli, db, EDM.Place);
        try                 { h.harvestAll(cb);         }
        catch (Throwable t) { t.printStackTrace();      }
        finally             { cli.close(); cb.finish(); }
    }
}
