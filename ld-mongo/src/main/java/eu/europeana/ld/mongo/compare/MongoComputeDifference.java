/**
 * 
 */
package eu.europeana.ld.mongo.compare;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.comp.ChangeModel;
import eu.europeana.ld.comp.ModelDifferenceCalculator;
import eu.europeana.ld.mongo.MongoEDMHarvester;
import eu.europeana.ld.mongo.MongoEDMParser;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Jan 2017
 */
public class MongoComputeDifference
{
    private static int DEF_BATCHSIZE = 1000;

    private MongoClient   _c1;
    private MongoClient   _c2;
    private MongoDatabase _db1;
    private MongoDatabase _db2;
    private ChangeModel   _cm = new ChangeModel();
    private ModelDifferenceCalculator _calculator
        = new ModelDifferenceCalculator();
    private MongoEDMParser _parser = new MongoEDMParser();

    
    public MongoComputeDifference(MongoClient c1, MongoClient c2, String dbn)
    {
        _c1  = c1;
        _c2  = c2;
        _db1 = c1.getDatabase(dbn);
        _db2 = c2.getDatabase(dbn);
    }

    @Override
    public void finalize()
    {
        
    }

    public void compare()
    {
        compare("WebResource");
    }

    public void compare(String colName)
    {
        MongoCollection<Document> col1 = _db1.getCollection(colName);
        if ( col1 == null ) { return; }

        MongoCollection<Document> col2 = _db2.getCollection(colName);
        if ( col2 == null ) { return; }

        BasicDBObject proj = new BasicDBObject("about", 1);
        MongoCursor<Document> iter = col1.find().sort(proj).projection(proj)
                                         .noCursorTimeout(true)
                                         .batchSize(DEF_BATCHSIZE).iterator();
        try {
            String prev = null;
            while ( iter.hasNext() )
            {
                Document doc = iter.next();
                String id = doc.getString("about");
                if ( id.equals(prev) ) { continue; }

                Resource r1 = getResource(col1, id);
                Resource r2 = getResource(col2, id);
                _calculator.computeDiff(r1, r2, _cm);
                System.out.println(_cm);
                _cm.clear();

                prev = id;
            }
        }
        finally { iter.close(); }
    }

    protected Resource getResource(MongoCollection<Document> col, String id)
    {
        Model    m    = ModelFactory.createDefaultModel();
        Resource r    = null;
        long     size = 0;

        BasicDBObject filter = new BasicDBObject("about", id);
        MongoCursor<Document> iter = col.find(filter).noCursorTimeout(true)
                                        .batchSize(DEF_BATCHSIZE).iterator();
        try {
            while ( iter.hasNext() )
            {
                r = _parser.parse(iter.next(), m);
                if ( size != 0 && m.size() != size ) { /*logMismatch(id);*/ }
                size = m.size();
            }
        }
        finally { iter.close(); }

        return r;
    }

    //koen@knutt.com

    public static final void main(String[] args)
    {
        MongoClient c1 = new MongoClient("144.76.218.178"    , 27017);
        MongoClient c2 = new MongoClient("mongo1.eanadev.org", 27017);
        MongoComputeDifference c = new MongoComputeDifference(c1, c2, "europeana");
        c.compare();
        c.finalize();
    }
}
