/**
 * 
 */
package eu.europeana.ld.mongo.io;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.europeana.ld.mongo.MongoClassDef;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Jun 2017
 */
public class MongoEntityWriter extends MongoEDMWriter
{
    public Document writeResource(Resource r, Document doc)
    {
        MongoClassDef def = getClassDefinition(r);
        if ( def == null ) { return doc; }

        doc.put("codeUri"       , r.getURI());
        Document representation = super.writeResource(r, new Document());
        doc.put("representation", representation);
        doc.put("entityType"    , def.getJavaClassAbbr(def.getJavaClass()));

        Object sameAs     = representation.get("owlSameAs");
        Object exactMatch = representation.get("exactMatch");
        doc.put("owlSameAs"     , (sameAs == null ? exactMatch : sameAs));

        return doc;
    }

}
