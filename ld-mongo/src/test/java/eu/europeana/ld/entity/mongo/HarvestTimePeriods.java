/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.ResourceCallback.Status;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.io.XMLIterativeResourceWriter;
import eu.europeana.ld.edm.io.XMLRecordWriter;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestTimePeriods
{
    public static final void main(String[] args) throws IOException
    {
        MongoClientOptions options= new MongoClientOptions.Builder().socketKeepAlive(true).build();
        MongoClient   cli = new MongoClient("136.243.103.29", options);
        //MongoClient   cli = new MongoClient("136.243.103.29");
        MongoDatabase db  = cli.getDatabase("annocultor_db");
        

        File dst = new File("D:/work/data/entities/time.xml");
        XMLIterativeResourceWriter writer = new XMLIterativeResourceWriter();
        writer.init(new FileOutputStream(dst));

        ResourceCallback cb = new ResourceCallback() {

            @Override
            public void handle(Resource r, Status s)
            {
                try                   { writer.write(r);               }
                catch (IOException e) { throw new RuntimeException(e); }
                finally               { r.getModel().removeAll();      }
            }
        };

        MongoEntityHarvester h = new MongoEntityHarvester(cli, db, EDM.TimeSpan);
        try                 { h.harvestAll(cb);                        }
        catch (Throwable t) { t.printStackTrace();                     }
        finally             { IOUtils.closeQuietly(writer); h.close(); }

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }

    private static class FilterCallback implements ResourceCallback
    {
        private ResourceCallback _cb;

        public FilterCallback(ResourceCallback cb) { _cb = cb; }

        @Override
        public void handle(Resource r, Status s)
        {
            StmtIterator iter = r.listProperties(SKOS.prefLabel);
            while ( iter.hasNext() )
            {
                String str = iter.next().getLiteral().getString();
                if ( StringUtils.isNumeric(str) ) { return; }
                _cb.handle(r, s); return;
            }
        }
        
    }
}
