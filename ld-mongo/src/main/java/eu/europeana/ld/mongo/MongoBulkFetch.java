/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.ObjectIdCodec;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 20 Jul 2018
 */
public class MongoBulkFetch
{
    private MongoDatabase _db;

    public MongoBulkFetch(MongoDatabase db) { _db = db; }

    public Map<DBRef,Document> fetch(Object obj)
    {
        Map<DBRef,Document> map = new HashMap();
        lookupRef(obj, map);

        if ( !map.isEmpty() ) { deref(map); }
        return map;
    }

    public void lookupRef(Object obj, Map<DBRef,Document> map)
    {
        if ( obj == null   ) { return; }

        if ( obj instanceof DBRef  ) {  map.put((DBRef)obj, null); return; }

        if ( obj instanceof List   )
        {
            for ( Object o : (List)obj ) { lookupRef(o, map); }
            return;
        }

        if ( obj instanceof Map   )
        {
            for ( Object o : ((Map)obj).values() ) { lookupRef(o, map); }
        }
    }

    public void deref(Map<DBRef,Document> map)
    {
        for ( Map.Entry<DBRef,Document> entry : map.entrySet() )
        {
            if ( entry.getValue() != null ) { continue; }

            deref(map, entry.getKey().getCollectionName());
        }
    }

    public void deref(Map<DBRef,Document> map, String colName)
    {
        Map<Object,DBRef> list = new HashMap();
        for ( DBRef ref : map.keySet() )
        {
            if ( !colName.equals(ref.getCollectionName()) ) { continue; }
            list.put(ref.getId(), ref);
        }

        MongoCollection<Document> col = _db.getCollection(colName);
        if ( list.size() == 1 )
        { 
            for ( Object id : list.keySet() )
            {
                map.put(list.get(id), fetch(col, id));
            }
            return;
        }

        MongoCursor<Document> iter = col.find(
            new BasicDBObject("_id", Collections.singletonMap("$in", list.keySet())))
            .batchSize(list.size()).iterator();
        try {
            while ( iter.hasNext() )
            {
                Document doc = iter.next();
                map.put(list.get(doc.get("_id")), doc);
            }
        }
        finally { iter.close(); }
    }

    public Document fetch(MongoCollection<Document> col, Object id)
    {
        return col.find(new BasicDBObject("_id", id)).first();
    }

    private void checkNulls(Map<DBRef,Document> map)
    {
        for ( Document doc : map.values() )
        {
            if ( doc == null ) { System.err.println(doc); }
        }
    }
}
