/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.ResourceCallback.Status;
import eu.europeana.ld.edm.CC;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.EuropeanaDataUtils;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.harvester.LDHarvester;

import static org.apache.jena.util.ResourceUtils.*;
import static eu.europeana.ld.mongo.MongoEDMConstants.*;
import static eu.europeana.ld.mongo.MongoClassDef.*;
import static eu.europeana.ld.mongo.MongoUtils.*;
import static eu.europeana.ld.mongo.TechnicalMetadataUtils.*;
import static eu.europeana.ld.iri.IRISupport.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 14 Apr 2016
 */
public class MongoEDMHarvesterOld implements MongoHarvester
{
    private static Logger _log = Logger.getLogger(LDHarvester.class);
    private static Map<Resource,String> _tableMap = new HashMap();

    static {
        _tableMap.put(EDM.Agent               , "Agent");
        _tableMap.put(ORE.Aggregation         , "Aggregation");
        _tableMap.put(SKOS.Concept            , "Concept");
        _tableMap.put(SKOS.ConceptScheme      , "ConceptScheme");
        _tableMap.put(EDM.EuropeanaAggregation, "EuropeanaAggregation");
        _tableMap.put(EDM.Event               , "Event");
        _tableMap.put(CC.License              , "License");
        _tableMap.put(EDM.PhysicalThing       , "PhysicalThing");
        _tableMap.put(EDM.Place               , "Place");
        _tableMap.put(EDM.ProvidedCHO         , "ProvidedCHO");
        //_tableMap.put(EDM.ProvidedCHO         , "Service");
        _tableMap.put(EDM.TimeSpan            , "Timespan");
        _tableMap.put(EDM.WebResource         , "WebResource");
        //_tableMap.put(EDM.WebResource         , "WebResourceMetaInfo");
        //_tableMap.put(EDM.WebResource         , "record");
    }

    private MongoClient        _cli;
    private MongoDatabase      _db;
    private Resource           _entityClass;
    private MongoEDMParser     _parser = new MongoEDMParser();
    private BasicDBObject      _sort   = null;
    private Collection<String> _filter = Collections.EMPTY_LIST;


    public MongoEDMHarvesterOld(MongoClient cli, MongoDatabase db
                           , Resource entityClass, boolean sort)
    {
        _cli         = cli;
        _db          = db;
        _entityClass = entityClass;
        if ( sort ) { _sort   = new BasicDBObject(ABOUT, 1); }
    }

    public void setFilter(Collection<String> filter) { _filter = filter; }


    /***************************************************************************
     * Interface LDHarvester
     **************************************************************************/

    @Override
    public Resource harvest(String uri)
    {
        return fetchOne(uri, ModelFactory.createDefaultModel(), null);
    }

    @Override
    public Model    harvest(Collection<String> uris)
    {
        return fetchMany(uris, ModelFactory.createDefaultModel(), null);
    }

    @Override
    public Model    harvestAll()
    {
        return fetchAll(ModelFactory.createDefaultModel(), null);
    }


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
     * Private Methods
     **************************************************************************/

    private String normalizeURI(String uri)
    {
        if ( uri.startsWith(EuropeanaDataUtils.NS) ) {
            String path = uri.substring(EuropeanaDataUtils.NS.length()-1);

            if ( path.startsWith("/place/")   ) { return uri; }
            if ( path.startsWith("/concept/") ) { return uri; }
            if ( path.startsWith("/agent/")   ) { return uri; }
            if ( path.startsWith("/time/")    ) { return uri; }

            if ( _entityClass != null       ) { return path;              }
            if ( path.startsWith("/item/")  ) { return path.substring(5); }
        }
        return uri;
    }

    private MongoCollection<Document> getCollection()
    {
        String table = _tableMap.get(_entityClass);
        return _db.getCollection(table == null ? "record" : table);
    }


    /***************************************************************************
     * Private Methods - Fetch All
     **************************************************************************/

    private Model fetchAll(Model model, ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(col.count(), 0);

        MongoCursor<Document> iter = sort(col.find())
                                     .noCursorTimeout(true)
                                     .batchSize(DEF_BATCHSIZE).iterator();
        try {
            while ( iter.hasNext() )
            {
                status.cursor++;
                Document doc = iter.next();

                String id = doc.getString(ABOUT);
                if ( _filter.contains(id) ) { continue; }

                Resource r = isRecord(doc) ? parseRecord(doc, model)
                                           : _parser.parse(doc, model);
                logSuccess(id, status);

                if ( cb != null ) { cb.handle(id, r, status); }
            }
        }
        finally { iter.close(); }

        return model;
    }


    /***************************************************************************
     * Private Methods - Fetch Many
     **************************************************************************/

    private Model fetchMany(Collection<String> uris, Model model, ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(uris.size(), 0);

        BasicDBObject filter = new BasicDBObject();
        for ( String uri : uris )
        {
            status.cursor++;
            String id = normalizeURI(uri);
            if ( _filter.contains(id) ) { continue; }

            filter.put(ABOUT, id);

            Document doc = col.find(filter).first();
            if ( doc == null ) { logNotFound(id, status); continue; }

            Resource r   = isRecord(doc) ? parseRecord(doc, model)
                                         : _parser.parse(doc, model);
            logSuccess(id, status);

            if ( cb != null ) { cb.handle(uri, r, status); }
        }
        return model;
    }

    private Model fetchMany(Bson filter, Model model, ResourceCallback cb)
    {
        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(col.count(filter), 0);

        MongoCursor<Document> iter = sort(col.find(filter))
                                    .noCursorTimeout(true)
                                    .batchSize(DEF_BATCHSIZE).iterator();
        try {
            while ( iter.hasNext() )
            {
                status.cursor++;
                Document doc = iter.next();

                String id = doc.getString(ABOUT);
                if ( _filter.contains(id) ) { continue; }

                Resource r = isRecord(doc) ? parseRecord(doc, model)
                                             : _parser.parse(doc, model);
                logSuccess(id, status);

                if ( cb != null ) { cb.handle(id, r, status); }
            }
        }
        finally { iter.close(); }

        return model;
    }


    /***************************************************************************
     * Private Methods - Fetch One
     **************************************************************************/

    private Resource fetchOne(String uri, Model model, ResourceCallback cb)
    {
        String id = normalizeURI(uri);
        if ( _filter.contains(id) ) { return null; }

        MongoCollection<Document> col = getCollection();
        if ( col == null ) { return null; }

        Status status = new Status(1, 1);

        BasicDBObject filter = new BasicDBObject(ABOUT, id);
        Document      doc    = col.find(filter).batchSize(1).first();
        if ( doc == null ) { logNotFound(id, status); return null; }

        Resource r   = isRecord(doc) ? parseRecord(doc, model)
                                     : _parser.parse(doc, model);
        logSuccess(id, status);

        if ( cb != null ) { cb.handle(id, r, status); }
        return r;
    }


    /***************************************************************************
     * Private Methods - Record
     **************************************************************************/

    private FindIterable<Document> sort(FindIterable<Document> doc)
    {
        return ( _sort == null ? doc : doc.sort(_sort) );
    }

    private boolean isRecord(Document doc)
    {
        return doc.get(CLASSNAME)
                  .equals("eu.europeana.corelib.solr.bean.impl.FullBeanImpl");
    }

    private Resource parseRecord(Document doc, Model model)
    {
        parseCollectionName(doc, model);

        List<Document> list = fetch(doc, PROVIDED_CHOS, PROXIES
                                       , AGGREGATIONS, EUROPEANA_AGGREGATION
                                       , CONCEPTS, PLACES, AGENTS
                                       , TIMESPANS, LICENSES, SERVICES);
        for ( Document nested : list ) { parseNestedEntity(nested, model); }

        String   id = "http://data.europeana.eu/item" + doc.getString(ABOUT);
        Resource r  = model.getResource(id);

        assureReferentialIntegrity(r);

        return r;
    }

    private void parseCollectionName(Document doc, Model model)
    {
        String id = "http://data.europeana.eu/aggregation/europeana"
                  + doc.getString(ABOUT);
        Resource aggr = model.getResource(id);
        parseValue(aggr, EDM.datasetName, doc.get(EUROPEANA_COLLECTION_NAME));
    }

    private void parseNestedEntity(Document doc, Model model)
    {
        _parser.parse(doc, model);

        String cn = doc.getString(CLASSNAME);
        if ( CLASS_AGGREGATION.equals(cn) ) { parseWebResources(doc, model); }
    }

    private void parseValue(Resource r, Property p, Object o)
    {
        if ( o == null             ) { return; }
        if ( o instanceof String   ) { parseValue(r, p, (String)o);  return; }
      //if ( o instanceof Boolean  ) { parseValue(r, p, (Boolean)o); return; }
      //if ( o instanceof Double  ) { parseValue(r, p, (Double)o);   return; }
        if ( o instanceof List     ) { parseValue(r, p, (List)o);    return; }

        _log.error("Unknown object: " + o + " of type: " + o.getClass());
    }

    private void parseValue(Resource r, Property p, String str)
    {
        if ( str.trim().isEmpty() ) { return; }
        r.addLiteral(p, str);
    }

    private void parseValue(Resource r, Property p, List list)
    {
        for ( Object o : list ) { parseValue(r, p, o); }
    }


    /***************************************************************************
     * Private Methods - Referential Integrity
     **************************************************************************/

    public void assureReferentialIntegrity(Resource r)
    {
        String mrIRI = r.getURI();
        String dsIRI = getDatasetID(mrIRI);
        Model  model = r.getModel();

        Map<String,String> map = new TreeMap();
        ResIterator iter = model.listResourcesWithProperty(RDF.type);
        try {
            while ( iter.hasNext() )
            {
                String iri = iter.next().getURI();
                if ( isAbsoluteIRI(iri) ) { continue; }

                String newIRI = fixRelativeIRI(iri, dsIRI);

                if ( newIRI != iri )
                {
                    logRenResource(mrIRI, iri, newIRI);
                    renameResource(model.getResource(iri), newIRI);
                }
                map.put(iri, newIRI);
            }
        }
        finally { iter.close(); }

        if ( map.isEmpty() ) { return; }

        for ( Statement stmt :  model.listStatements().toList() )
        {
            RDFNode obj = stmt.getObject();
            if ( !obj.isLiteral()      ) { continue; }

            String value = obj.asLiteral().getString();
            String iri   = map.get(value);
            if ( iri == null ) { continue; }

            model.add(stmt.getSubject(), stmt.getPredicate()
                    , model.getResource(iri));
            model.remove(stmt);
            logFixedRef(mrIRI, value, iri);
        }
    }

    private String getDatasetID(String recordIRI)
    {
        int i = recordIRI.lastIndexOf("/");
        return (i > 0 ? recordIRI.substring(0, i) : recordIRI);
    }

    private String fixRelativeIRI(String iri, String dsIRI)
    {
        if ( iri.startsWith("/")
          || iri.startsWith("#") ) { return dsIRI + iri; }
        
        if ( iri.startsWith("../") 
          || iri.startsWith("./") ) { return dsIRI + "/" + iri; }

        int i = iri.indexOf("/");
        if ( i > 0 && iri.lastIndexOf(':', i) < 0) { return dsIRI + "/" + iri; }

        return iri;
    }


    /***************************************************************************
     * Private Methods - Web Resources
     **************************************************************************/

    private void parseWebResources(Document aggr, Model model)
    {
        String recordId = aggr.getString(AGGREGATED_CHO).replace("/item","");

        for ( Document wr :  fetch(aggr, WEB_RESOURCES))
        {
            parseWebResource(wr, recordId, model);
        }
    }

    private Resource parseWebResource(Document doc, String recordId
                                    , Model model)
    {
        return _parser.parse(appendTechMeta(recordId, doc), model);
    }

    private Document appendTechMeta(String recordId, Document docWr)
    {
        String   id    = getTechMetaID(recordId, docWr.getString(ABOUT));
        Document docTm = fetch(_db.getCollection("WebResourceMetaInfo"), id);
        if ( docTm == null ) { return docWr; }

        Document ret = appendFields(docTm, docWr, "imageMetaInfo"
                                  , "audioMetaInfo", "videoMetaInfo"
                                  , "textMetaInfo");
        if ( !ret.containsKey("fileSize") ) { logNoTechMeta(recordId, id); }
        return ret;
    }
    

    private Document appendFields(Document src, Document trg, String... fields)
    {
        for ( String field : fields )
        {
            Object obj = src.get(field);
            if ( obj == null || !(obj instanceof Map) ) { continue; }
    
            trg.putAll((Map)obj);
        }
        return trg;
    }


    /***************************************************************************
     * Private Methods - Fetch References
     **************************************************************************/

    private List<Document> fetch(Document doc, String... fields)
    {
        List empty = Collections.EMPTY_LIST;
        List list  = empty;
        for ( String field : fields )
        {
            Object obj = doc.get(field);
            if ( obj == null   ) { continue; }

            if ( list == empty ) { list = new ArrayList<Document>(10); }
            fetch(list, obj);
        }
        return list;
    }

    private void fetch(List<Document> ret, Object obj)
    {
        if ( obj instanceof List   ) { fetch(ret, (List)obj);  }
        if ( obj instanceof DBRef  ) { fetch(ret, (DBRef)obj); }
    }

    private void fetch(List<Document> ret, List list)
    {
        for ( Object o : list ) { fetch(ret, o); }
    }

    private void fetch(List<Document> ret, DBRef ref)
    {
        String   col = ref.getCollectionName();
        Document doc = fetch(_db.getCollection(col), (ObjectId)ref.getId());
        if (doc != null) { ret.add(doc); }
    }

    private Document fetch(MongoCollection<Document> col, Object id)
    {
        if (col == null) { return null; }
        return col.find(new BasicDBObject("_id", id)).first();
    }


    /***************************************************************************
     * Private Methods - Logging
     **************************************************************************/

    private void logRenResource(String recordId, String iri, String newIri)
    {
        if (!_log.isInfoEnabled()) { return; }

        _log.info("Renaming resource <" + iri + ">"
                + " to <" + newIri + ">"
                + " for record <" + recordId + ">");
    }


    private void logFixedRef(String recordId, String value, String iri)
    {
        if (!_log.isInfoEnabled()) { return; }

        _log.info("Fixed resource reference \"" + value + "\""
                + " to <" + iri + ">"
                + " for record <" + recordId + ">");
    }

    private void logNoTechMeta(String recordId, String id)
    {
        _log.error("Technical metadata empty for record <" + recordId
                + ">, media resource <" + id + ">");
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
