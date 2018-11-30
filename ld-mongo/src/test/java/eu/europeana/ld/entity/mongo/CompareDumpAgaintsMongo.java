/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.mongo.MongoEntityHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class CompareDumpAgaintsMongo implements ResourceCallback
{
    private Model       _model;
    private Set<String> _processed = new HashSet();

    public CompareDumpAgaintsMongo(Model model) { _model = model; }

    @Override
    public void handle(Resource r1)
    {
        String dbp = getDBPediaURI(r1);
        if ( dbp == null ) {
            System.out.println("Cannot find DBpedia URI for: " + r1.getURI());
            return;
        }

        _processed.add(dbp);

        r1 = ResourceUtils.renameResource(r1, dbp);
        Resource r2  = fixResource(_model.getResource(dbp));

        if ( isEmpty(r2) ) {
            System.out.println("Missing resource in Dataset: " + dbp);
            return;
        }

        r2 = getNewResource(r2).getResource(dbp);
        if ( compareResources(r1, r2) ) { return; }

        System.out.println("Different resource: " + dbp);
    }

    public void run()
    {
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");

        try { new MongoEntityHarvester(db, true).harvest(EDM.Agent, this); }
        catch (Throwable t) { t.printStackTrace(); }
        finally { client.close(); }

        ResIterator iter = _model.listResourcesWithProperty(RDF.type, EDM.Agent);
        while ( iter.hasNext() )
        {
            String uri = iter.next().getURI();
            if ( _processed.contains(uri) ) { continue; }

            System.out.println("Missing resource in Mongo: " + uri);
        }
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

    protected boolean compare(Resource r1, Resource r2)
    {
        Model m1 = getNewResource(r1);
        Model m2 = getNewResource(r2);
        return m1.isIsomorphicWith(m2);
    }

    protected Model getNewResource(Resource r)
    {
        Model model = ModelFactory.createDefaultModel();
        model.add(r.listProperties());
        return model;
    }

    protected boolean isEmpty(Resource r)
    {
        return !r.hasProperty(RDF.type);
    }

    protected Resource fixResource(Resource r)
    {
        StmtIterator iter = r.listProperties();
        try {
            while ( iter.hasNext() )
            {
                Statement stmt = iter.next();
                RDFNode node = stmt.getObject();
                if ( !node.isLiteral() ) { continue; }

                Literal l   = node.asLiteral();
                String  str = l.getString();
                if ( !str.startsWith("http://") ) { continue; }

                Property p = stmt.getPredicate();
                iter.remove();
                r.addProperty(p, p.getModel().getResource(str));
                return fixResource(r);
            }
        }
        finally { iter.close(); }
        return r;
    }

    protected boolean compareResources(Resource r1, Resource r2)
    {
        Model m1 = r1.getModel();
        Model m2 = r2.getModel();
        StmtIterator iter;

        iter = r1.listProperties();
        try {
            while ( iter.hasNext() )
            {
                Statement stmt = iter.next();
                if ( m2.contains(stmt) ) { continue; }
    
                System.out.println("[" + stmt.getResource().getURI()
                                 + "," + stmt.getPredicate().getURI()
                                 + "," + stmt.getObject());
                return false;
            }
        }
        finally { iter.close(); }

        iter = r2.listProperties();
        try {
            while ( iter.hasNext() )
            {
                Statement stmt = iter.next();
                if ( m1.contains(stmt) ) { continue; }
    
                System.out.println("[" + stmt.getResource().getURI()
                                 + "," + stmt.getPredicate().getURI()
                                 + "," + stmt.getObject());
                return false;
            }
        }
        finally { iter.close(); }

        return true;
    }

    public static final void main(String[] args) throws IOException
    {
        Model m = ModelFactory.createDefaultModel()
                              .read("d:/work/data/entities/agents_old.xml");
        new CompareDumpAgaintsMongo(m).run();
    }
}
