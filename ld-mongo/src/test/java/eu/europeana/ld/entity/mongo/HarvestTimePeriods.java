/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;
import eu.europeana.ld.mongo.callback.FileCallback;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class HarvestTimePeriods
{
    public static final void main(String[] args) throws IOException
    {
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");
        

        File dst = new File("D:/work/data/entities/named_timespan.xml");
        FileCallback   fcb = new FileCallback(dst);
        FilterCallback cb  = new FilterCallback(fcb);
        try { new MongoEntityHarvester(db, EDM.TimeSpan, true).harvestAll(cb); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }
        fcb.finish();

        //ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
    }

    private static class FilterCallback implements HarvesterCallback
    {
        private HarvesterCallback _cb;

        public FilterCallback(HarvesterCallback cb) { _cb = cb; }

        @Override
        public void handle(Resource r)
        {
            StmtIterator iter = r.listProperties(SKOS.prefLabel);
            while ( iter.hasNext() )
            {
                String str = iter.next().getLiteral().getString();
                if ( StringUtils.isNumeric(str) ) { return; }
                _cb.handle(r); return;
            }
        }
        
    }
}
