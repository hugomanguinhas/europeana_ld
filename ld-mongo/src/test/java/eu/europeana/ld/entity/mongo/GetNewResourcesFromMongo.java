/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.mongo.MongoEntityHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class GetNewResourcesFromMongo implements ResourceCallback
{
    private Model       _src;
    private Model       _dst;

    public GetNewResourcesFromMongo(Model src, Model dst)
    {
        _src = src;
        _dst = dst;
    }

    @Override
    public void handle(Resource r1)
    {
        Model    m1  = r1.getModel();
        String   dbp = getDBPediaURI(r1);
        if ( dbp == null ) {
            System.out.println("Cannot find DBpedia URI for: " + r1.getURI());
            return;
        }

        Resource r2  = _src.getResource(dbp);
        if ( !isEmpty(r2) ) { return; }

        _dst.add(m1);
    }

    public void run()
    {
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");

        try { new MongoEntityHarvester(db, true).harvest(EDM.Agent, this); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }

        
    }

    protected String getDBPediaURI(Resource r)
    {
        StmtIterator iter = r.listProperties(OWL.sameAs);
        try {
            while(iter.hasNext())
            {
                Statement stmt = iter.next();
                String    uri  = stmt.getObject().asResource().getURI();
                if ( !uri.startsWith("http://dbpedia.org/resource/") ) { continue; }

                iter.remove();
                return uri;
            }
        }
        finally { iter.close(); }

        return null;
    }

    protected boolean isEmpty(Resource r)
    {
        return !r.hasProperty(RDF.type);
    }

    public static final void main(String[] args) throws IOException
    {
        Model dst = ModelFactory.createDefaultModel();
        Model src = ModelFactory.createDefaultModel()
                                .read("d:/work/data/entities/agents_dbpedia_src.xml");
        new GetNewResourcesFromMongo(src, dst).run();
        JenaUtils.store(dst, new File("d:/work/data/entities/agents_new.xml"));
    }
}
