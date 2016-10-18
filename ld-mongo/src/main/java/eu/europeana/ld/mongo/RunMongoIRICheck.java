/**
 * 
 */
package eu.europeana.ld.mongo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.iri.Violation;
import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 Sep 2016
 */
public class RunMongoIRICheck
{
    private static Logger log = Logger.getLogger(MongoEDMHarvester.class);

    private CSVPrinter    _ps;
    private MongoDatabase _db;
    private String        _recordID;
    private IRIFactory    _fact = IRIFactory.iriImplementation();

    public static void main(String[] args) throws IOException
    {
        if ( args.length < 1 ) {
            System.out.println("Not enough parameters!"); return;
        }
        String      url = args[0];
        PrintStream ps  = ( args.length >= 2 ? new PrintStream(new File(args[1]), "UTF-8")
                                             : System.out );

        MongoClient client = new MongoClient(url, 27017);
        try {
            MongoDatabase db = client.getDatabase("europeana");
            new RunMongoIRICheck(db).run(ps);
        }
        finally { client.close(); }
    }

    public RunMongoIRICheck(MongoDatabase db)
    {
        _db = db;
    }

    public void run(PrintStream ps) throws IOException
    {
        _ps = new CSVPrinter(ps, CSVFormat.EXCEL);
        try {
            MongoCollection<Document> col     = _db.getCollection("record");
            long size = col.count();

            FindIterable<Document> colIter = col.find().noCursorTimeout(true);
            MongoCursor<Document>  iter    = colIter.iterator();
            long cursor = 0;
            try {
                while (iter.hasNext())
                {
                    cursor++;
                    if ( cursor % 1000 == 0 ) {
                        log.info("Validated [" + cursor + "] of [" + size + "]");
                    }
                    Document doc = iter.next();
                    _recordID = "http://data.europeana.eu/item"
                              + doc.getString("about");

                  //validateEntities(doc);
                    validateAggregations(doc);

                    _recordID = null;
                }
                ps.flush();
            }
            finally { iter.close(); }
        }
        finally {
            IOUtils.closeQuietly(_ps);
            _ps = null;
        }
    }


    /***************************************************************************
     * Private Methods - Fetch References
     **************************************************************************/

    private List<Document> fetch(Document doc, String... fields)
    {
        List list = new ArrayList<Document>(10);
        for ( String field : fields ) { fetch(list, doc.get(field)); }
        return list;
    }

    private void fetch(List<Document> ret, Object obj)
    {
        if ( obj == null           ) { return;                 }
        if ( obj instanceof List   ) { fetch(ret, (List)obj);  }
        if ( obj instanceof DBRef  ) { fetch(ret, (DBRef)obj); }
    }

    private void fetch(List<Document> ret, List list)
    {
        for ( Object o : list ) { fetch(ret, o); }
    }

    private void fetch(List<Document> ret, DBRef ref)
    {
        String                    col = ref.getCollectionName();
        MongoCollection<Document> doc = _db.getCollection(col);
        if (doc == null) { return; }

        BasicDBObject         key  = new BasicDBObject("_id", ref.getId());
        MongoCursor<Document> iter = doc.find(key).iterator();
        try {
            while ( iter.hasNext() ) { ret.add(iter.next()); }
        }
        finally { iter.close(); }
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private void validateAggregations(Document record) throws IOException
    {
        for (Document doc : fetch(record,"aggregations","europeanaAggregation"))
        {
            for ( Document wres: fetch(doc,"webResources") ) { validate(wres); }
        }
    }

    private void validateEntities(Document record)
            throws IOException
    {
        List<Document> list = fetch(record, "concepts", "places", "agents"
                                          , "timespans");
        for (Document entity : list) { validate(entity); }
    }

    private void validate(Document doc) throws IOException
    {
        String str  = doc.getString("about").trim();
        try {
            IRI iri = _fact.create(str);
            if ( !iri.hasViolation(false) ) { return; }

            Iterator<Violation> iter = iri.violations(false);
            while ( iter.hasNext() ) { printViolation(str, iter.next()); }
        }
        catch (IRIException e) { printException(str, e); }
    }

    private void printViolation(String str, Violation v) throws IOException
    {
        _ps.printRecord(_recordID, str, v.getViolationCode(), v.codeName());
    }

    private void printException(String str, IRIException e)
    {
        log.error("Exception validating IRI <" + str + ">", e);
    }
}
