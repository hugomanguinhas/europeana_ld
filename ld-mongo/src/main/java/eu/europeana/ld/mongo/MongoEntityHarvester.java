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
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.ResourceCallback.Status;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.harvester.LDHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2016
 */
public class MongoEntityHarvester extends MongoEnrichmentConstants 
                                  implements MongoHarvester
{
    private static Logger _log = Logger.getLogger(LDHarvester.class);
    private static int DEF_BATCHSIZE = 1000;

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
    public void harvestAll(ResourceCallback cb)
    {
        fetchAll(ModelFactory.createDefaultModel(), cb);
    }

    @Override
    public void harvest(String uri, ResourceCallback cb)
    {
        fetchOne(uri, ModelFactory.createDefaultModel(), cb);
    }

    @Override
    public void harvest(Collection<String> uris, ResourceCallback cb)
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
     * Public Methods
     **************************************************************************/

    public void harvestBySearch(String query, ResourceCallback cb)
    {
        harvestBySearch(BasicDBObject.parse(query), cb);
    }

    public void harvestBySearch(Bson filter, ResourceCallback cb)
    {
        fetchMany(filter, ModelFactory.createDefaultModel(), cb);
    }


    /***************************************************************************
     * Private Methods - Fetch All
     **************************************************************************/

    private Model fetchAll(Model model, ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col   == null ) { return null; }

        MongoCollection<Document> tList = _db.getCollection(TBL_TERM_LIST);
        if ( tList == null ) { return null; }

        Status status = new Status(col.count(), 0);

        MongoCursor<String> iter = col.distinct(FIELD_CODE_URI, String.class)
                                      .batchSize(DEF_BATCHSIZE).iterator();
        try {
            while ( iter.hasNext() )
            {
                status.cursor++;
                String   id = iter.next();

                Resource r  = fetchEntity(tList, model.getResource(id));
                logSuccess(id, status);

                if ( r != null ) { cb.handle(id, r, status); }
            }
        }
        finally { iter.close(); }

        return model;
    }


    /***************************************************************************
     * Private Methods - Fetch Many
     **************************************************************************/

    private Model fetchMany(Collection<String> uris, Model model
                          , ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col   == null ) { return null; }

        MongoCollection<Document> tList = _db.getCollection(TBL_TERM_LIST);
        if ( tList == null ) { return null; }

        Status status = new Status(uris.size(), 0);

        for ( String uri : uris )
        {
            status.cursor++;

            Resource r = fetchEntity(tList, model.getResource(uri));
            if ( r == null ) { logNotFound(uri, status); continue; }

            logSuccess(uri, status);
            if ( cb != null ) { cb.handle(uri, r, status); }
        }
        return model;
    }

    //TODO
    private Model fetchMany(Bson filter, Model model, ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        MongoCollection<Document> tList = _db.getCollection(TBL_TERM_LIST);
        if ( tList == null ) { return null; }

        Status status = new Status(col.count(filter), 0);

        return model;
    }


    /***************************************************************************
     * Private Methods - Fetch One
     **************************************************************************/

    private Resource fetchOne(String uri, Model model, ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        MongoCollection<Document> tList = _db.getCollection(TBL_TERM_LIST);
        if ( tList == null ) { return null; }

        Status status = new Status(1, 1);

        Resource r = fetchEntity(tList, model.getResource(uri));
        if ( r == null ) { logNotFound(uri, status); return null; }

        logSuccess(uri, status);
        if ( cb != null ) { cb.handle(uri, r, status); }

        return r;
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private MongoCollection<Document> getCollection()
    {
        String table = RES2TABLE.get(_entityClass);
        return ( table == null ? null : _db.getCollection(table) );
    }

    private Resource fetchEntity(MongoCollection<Document> tList, Resource r)
    {
        String uri = r.getURI();
        MongoCursor<Document> iter = tList.find(new Document(FIELD_CODE_URI, uri))
                                          .noCursorTimeout(true).iterator();
        try {
            if ( !iter.hasNext() ) { logUnknown(uri); return null; }

            r = _parser.parse(iter.next(), r.getModel());

            if ( iter.hasNext() ) { logDuplicate(uri); }
        }
        finally { iter.close(); }

        return r;
    }


    /***************************************************************************
     * Private Methods - Logging
     **************************************************************************/

    private void logUnknown(String id)
    {
        _log.error("Could not find entry with uri: " + id);
    }

    private void logDuplicate(String id)
    {
        _log.error("Found duplicate entry for uri: " + id);
    }

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
