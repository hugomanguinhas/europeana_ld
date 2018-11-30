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
import eu.europeana.ld.edm.io.EDMDatasetFileNaming;
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
public class HarvestDataset
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient   cli = new MongoClient("mongo1.ingest.eanadev.org", 27017);
        MongoDatabase db  = cli.getDatabase("europeana_production_preview_1");


        MongoEDMHarvester harvester = new MongoEDMHarvester(cli, db, null, true);
        File dstZip = new File("D:/work/incoming/pablo/dataset/2033502.zip");
        LDStore store = new ZipLDStore(dstZip, new XMLRecordWriter()
                      , Lang.RDFXML, new EDMDatasetFileNaming());
        StoreCallback cb = new StoreCallback(store);
        cb.begin();
        try {
            harvester.harvestDataset(cb, "2033502");
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { cli.close(); cb.finish(); }
    }
}
