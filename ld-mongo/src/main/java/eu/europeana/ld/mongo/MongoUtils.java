/**
 * 
 */
package eu.europeana.ld.mongo;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Jun 2017
 */
public class MongoUtils
{
    public static int DEF_BATCHSIZE = 100;

    public static MongoCollection<Document> getMongoCollection(MongoDatabase db
                                                             , String name)
    {
        MongoIterable<String> iter = db.listCollectionNames();
        for ( String col : iter )
        {
            if ( col.equals(name) ) { return db.getCollection(name); }
        }

        db.createCollection(name);
        return db.getCollection(name);
    }
}
