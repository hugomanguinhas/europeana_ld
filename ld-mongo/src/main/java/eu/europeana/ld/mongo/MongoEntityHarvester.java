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
import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.harvester.HarvesterCallback;
import eu.europeana.ld.harvester.LDHarvester;
import eu.europeana.ld.harvester.HarvesterCallback.Status;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2016
 */
public class MongoEntityHarvester implements LDHarvester
{
    private static Logger _log = Logger.getLogger(LDHarvester.class);
    private static Map<Resource,String> _tableMap = new HashMap();

    static {
        _tableMap.put(EDM.Agent   , "people");
        _tableMap.put(EDM.Place   , "place");
        _tableMap.put(EDM.TimeSpan, "period");
        _tableMap.put(SKOS.Concept, "concept");
    }

    private MongoClient       _cli;
    private MongoDatabase     _db;
    private Resource          _entityClass;
    private MongoEntityParser _parser = new MongoEntityParser();


    public MongoEntityHarvester(MongoClient cli, MongoDatabase db
                              , Resource entityClass)
    {
        _cli              = cli;
        _db               = db;
        _entityClass      = entityClass;
    }


    /***************************************************************************
     * Interface LDHarvester
     **************************************************************************/

    @Override
    public void harvestAll(HarvesterCallback cb)
    {
        fetchAll(ModelFactory.createDefaultModel(), cb);
    }

    @Override
    public void harvest(String uri, HarvesterCallback cb)
    {
        fetchOne(uri, ModelFactory.createDefaultModel(), cb);
    }

    @Override
    public void harvest(Collection<String> uris, HarvesterCallback cb)
    {
        fetchMany(uris, ModelFactory.createDefaultModel(), cb);
    }

    @Override
    public Resource harvest(String uri)
    {
        return fetchOne(uri, ModelFactory.createDefaultModel(), null);
    }

    @Override
    public Model harvest(Collection<String> uris)
    {
        return fetchMany(uris, ModelFactory.createDefaultModel(), null);
    }

    @Override
    public Model harvestAll()
    {
        return fetchAll(ModelFactory.createDefaultModel(), null);
    }

    @Override
    public void close() { _cli.close(); }


    /***************************************************************************
     * Private Methods - Fetch All
     **************************************************************************/

    private Model fetchAll(Model model, HarvesterCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(col.count(), 0);

        MongoCursor<String> iter = col.distinct("codeUri", String.class)
                                      .iterator();
        try {
            while ( iter.hasNext() )
            {
                status.cursor++;
                String   id = iter.next();

                Resource r  = fetchEntity(_db, model.getResource(id));
                logSuccess(id, status);

                if ( r != null ) { cb.handle(r, status); }
            }
        }
        finally { iter.close(); }

        return model;
    }


    /***************************************************************************
     * Private Methods - Fetch Many
     **************************************************************************/

    private Model fetchMany(Collection<String> uris, Model model
                          , HarvesterCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(uris.size(), 0);

        for ( String uri : uris )
        {
            status.cursor++;

            Resource r = fetchEntity(_db, model.getResource(uri));
            if ( r == null ) { logNotFound(uri, status); continue; }

            logSuccess(uri, status);
            if ( cb != null ) { cb.handle(r, status); }
        }
        return model;
    }


    /***************************************************************************
     * Private Methods - Fetch One
     **************************************************************************/

    private Resource fetchOne(String uri, Model model, HarvesterCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(1, 1);

        Resource r = fetchEntity(_db, model.getResource(uri));
        if ( r == null ) { logNotFound(uri, status); return null; }

        logSuccess(uri, status);
        if ( cb != null ) { cb.handle(r, status); }

        return r;
    }


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
                _log.error("Could not find entry with uri: " + uri);
                return null;
            }

            r = _parser.parse(iter.next(), r.getModel());

            if ( iter.hasNext() ) {
                _log.error("Found duplicate entry for uri: " + uri);
            }
        }
        finally { iter.close(); }

        return r;
    }


    /***************************************************************************
     * Private Methods - Logging
     **************************************************************************/

    private void logSuccess(String id, Status status)
    {
        if (!_log.isInfoEnabled()) { return; }

        _log.info("Harvesting <" + id + "> "
                + status.cursor + " of " + status.total + ": DONE");
    }

    private void logNotFound(String id, Status status)
    {
        if (!_log.isInfoEnabled()) { return; }

        _log.info("Harvesting <" + id + "> "
                + status.cursor + " of " + status.total + ": NOT FOUND");
    }
}
