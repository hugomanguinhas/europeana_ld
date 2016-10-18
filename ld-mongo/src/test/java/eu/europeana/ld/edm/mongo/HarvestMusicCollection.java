/**
 * 
 */
package eu.europeana.ld.edm.mongo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;

import com.github.jsonldjava.utils.JsonUtils;
import com.jayway.jsonpath.JsonPath;
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
import eu.europeana.ld.store.LDStore;
import eu.europeana.ld.store.ZipLDStore;
import eu.europeana.mongo.callback.FileCallback;

import org.apache.jena.riot.EDMLang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestMusicCollection
{
    public static final void main(String[] args) throws IOException
    {
        File          src = new File("D:\\work\\incoming\\joris\\search.json");
        MongoClient   cli = new MongoClient("mongo1.eanadev.org", 27017);
        MongoDatabase db  = cli.getDatabase("europeana");

        Map m = deserialize(new FileInputStream(src));

        List<Map> fields = (List<Map>)JsonPath.read(m, "$.facets[0].fields");

        MongoEDMHarvester harvester = new MongoEDMHarvester(db, null, true);
        File dstZip = new File("D:/work/data/mongo/dataset.zip");
        LDStore store = new ZipLDStore(dstZip, new EDMXMLWriter()
                      , Lang.RDFXML, new EDMDataFileNaming());
        StoreCallback cb = new StoreCallback(store);
        cb.begin();
        try {
            int cursor = 0;
            for ( Map field : fields )
            {
                cursor++;
                String uri = (String)field.get("label");
                printMessage(cursor, fields.size(), uri);
                harvester.harvest(uri, cb);
            }
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { cli.close(); cb.finish(); }
    }

    private static void printMessage(int cursor, int total, String uri)
    {
        System.out.println("Harvesting [" + cursor + "] of [" + total + "]: "
                         + uri);
    }

    protected static Map deserialize(InputStream in) throws IOException
    {
        try {
            return (Map)JsonUtils.fromInputStream(in, "UTF-8");
        }
        finally { IOUtils.closeQuietly(in); }
    }
}
