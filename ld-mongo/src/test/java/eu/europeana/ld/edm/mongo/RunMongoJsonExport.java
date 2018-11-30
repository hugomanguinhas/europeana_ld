/**
 * 
 */
package eu.europeana.ld.edm.mongo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import com.github.jsonldjava.utils.JsonUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.io.EDMDatasetFileNaming;
import eu.europeana.ld.edm.io.TurtleRecordWriter;
import eu.europeana.ld.edm.io.XMLRecordWriter;
import eu.europeana.ld.harvester.StoreCallback;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.mongo.io.MongoEDMWriter;
import eu.europeana.ld.store.LDStore;
import eu.europeana.ld.store.ZipLDStore;

import org.apache.jena.riot.EDMLang;
import org.bson.Document;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class RunMongoJsonExport
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient     cli = new MongoClient("144.76.218.178", 27017);
//      MongoClient     cli = new MongoClient("mongo2.eanadev.org", 27017);
        MongoDatabase   db  = cli.getDatabase("europeana");

        MongoEDMHarvester harvester = new MongoEDMHarvester(cli, db, null, false);
        try {
            Resource r = harvester.harvest("/02030/MatrizNet_Objectos_ObjectosConsultar_aspx_IdReg_1041321");

            //JenaUtils.store(r.getModel(), System.out, "RDF/XML");
            Map doc = new MongoEDMWriter().writeModel(r.getModel(), new HashMap());
            
            JsonUtils.write(new OutputStreamWriter(System.out), doc);
        }
        catch (Throwable t) { t.printStackTrace(); }
        finally { cli.close();  }
    }
}
