/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.harvester.LDHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2016
 */
public class MongoEntityHarvester implements LDHarvester
{
    private static Map<Resource,String> _tableMap = new HashMap();

    static {
        _tableMap.put(EDM.Agent   , "people");
        _tableMap.put(EDM.Place   , "place");
        _tableMap.put(EDM.TimeSpan, "period");
        _tableMap.put(SKOS.Concept, "concept");
    }

    private MongoClient       _cli;
    private MongoDatabase     _db;
    private boolean           _cleanAfterHandle;
    private Resource          _entityClass;
    private MongoEntityParser _parser = new MongoEntityParser();


    public MongoEntityHarvester(MongoClient cli, MongoDatabase db
                              , Resource entityClass, boolean cleanAfterHandle)
    {
        _cli              = cli;
        _db               = db;
        _entityClass      = entityClass;
        _cleanAfterHandle = cleanAfterHandle;
    }


    /***************************************************************************
     * Interface LDHarvester
     **************************************************************************/

    @Override
    public void harvestAll(HarvesterCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return; }

        MongoCursor<String> iter = col.distinct("codeUri", String.class)
                                      .iterator();
        try {
            int i = 0;
            Model             model  = ModelFactory.createDefaultModel();
            while ( iter.hasNext() )
            {
                Resource r = fetchEntity(_db, model.getResource(iter.next()));
                if ( r != null ) { cb.handle(r); }

                if ( _cleanAfterHandle ) { model.removeAll(); }

                if ( ++i % 10000 == 0 ) { System.out.println("Harvested " + i + " items"); }
            }
        }
        finally { iter.close(); }
    }

    @Override
    public void harvest(String uri, HarvesterCallback cb)
    {
        Model    model = ModelFactory.createDefaultModel();
        Resource r     = fetchEntity(_db, model.getResource(uri));
        if ( r != null ) { cb.handle(r); }
    }

    @Override
    public void harvest(Collection<String> uris, HarvesterCallback cb)
    {
        Model             model  = ModelFactory.createDefaultModel();
        for ( String uri : uris )
        {
            Resource r = fetchEntity(_db, model.getResource(uri));
            if ( r != null ) { cb.handle(r); }
        }
    }

    @Override
    public Resource harvest(String uri)
    {
        throw new RuntimeException("Method not implemented!");
    }

    @Override
    public Model harvest(Collection<String> uris)
    {
        throw new RuntimeException("Method not implemented!");
    }

    @Override
    public Model harvestAll()
    {
        throw new RuntimeException("Method not implemented!");
    }

    @Override
    public void close() { _cli.close(); }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private MongoCollection<Document> getCollection()
    {
        String table = _tableMap.get(_entityClass);
        return ( table == null ? null : _db.getCollection(table) );
    }

    private Resource fetchEntity(MongoDatabase db, Resource r)
    {
        String uri = r.getURI();
        MongoCursor<Document> iter = db.getCollection("TermList")
                                       .find(new Document("codeUri", uri))
                                       .iterator();
        try {
            if ( !iter.hasNext() ) {
                System.err.println("Could not find entry with uri: " + uri);
                return null;
            }

            r = _parser.parse(iter.next(), r.getModel());

            if ( iter.hasNext() ) {
                System.err.println("Found duplicate entry for uri: " + uri);
            }
        }
        finally { iter.close(); }

        return r;
    }
}
