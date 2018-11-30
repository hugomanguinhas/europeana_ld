package eu.europeana.ld.mongo.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.europeana.ld.json.RewriteSupport;

import static org.apache.commons.lang3.StringUtils.*;
import static eu.europeana.ld.mongo.io.EDMMongoConstants.*;

/**
 */
public class EDMMongoNewWriter 
{
    private Map<String,String> _rewrites;

    public EDMMongoNewWriter(String url) throws IOException
    {
        _rewrites = new RewriteSupport().getUncompactRewrites(url);
    }

    public DBObject write(Model model, BasicDBObject doc)
    {
        for ( Resource c : CLASSES_2_LABEL.keySet() )
        {
            ResIterator iter = model.listResourcesWithProperty(RDF.type, c);
            if ( !iter.hasNext() ) { continue; }

            List<Object> list = new ArrayList(5);
            try {
                while ( iter.hasNext() )
                {
                    DBObject obj = write(iter.next(), new BasicDBObject());
                    if ( obj != null ) { list.add(obj); }
                }
                doc.append(CLASSES_2_LABEL.get(c), list);
            }
            finally { iter.close(); }
        }
        return doc;
    }

    public DBObject write(Resource resource, DBObject doc)
    {
        if ( resource == null || doc == null ) { return doc; }

        addMeta(resource, doc);

        StmtIterator iter = resource.listProperties();
        try {
            while ( iter.hasNext() )
            {
                Statement stmt  = iter.next();
                Property  p     = stmt.getPredicate();
                if ( p.equals(RDF.type) ) { continue; }

                newNode(stmt.getObject(), newProperty(p, doc));
            }
        }
        finally { iter.close(); }

        return doc;
    }

    private void addMeta(Resource resource, DBObject obj)
    {
        obj.put(FIELD_ID  , resource.getURI());

        Statement stmt  = resource.getProperty(RDF.type);
        if ( stmt == null ) { return; }

        Resource  type  = stmt.getResource();
        String    sType = _rewrites.get(type.getURI());
        obj.put(FIELD_TYPE, (sType == null ? type.getURI() : sType));
    }

    private String getPropertyKey(Property p)
    {
        String key = _rewrites.get(p.getURI());
        return (key == null ? p.getLocalName() : key);
    }

    private DBObject newProperty(Property p, DBObject cobj)
    {
        String   key  = getPropertyKey(p);
        DBObject pobj = (DBObject)cobj.get(key);
        if ( pobj == null ) {
            pobj = new BasicDBObject();
            cobj.put(key, pobj);
        }
        return pobj;
    }

    private void newNode(RDFNode node, DBObject obj)
    {
        if ( node.isURIResource() ) {
            newItem(obj, FIELD_RESOURCE).add(node.asResource().getURI());
            return;
        }
        if ( !node.isLiteral() ) { return; }

        Literal literal = node.asLiteral();
        String  lang    = literal.getLanguage();
        String  value   = literal.getString();
        if ( isEmpty(lang) ) { newItem(obj, FIELD_LITERAL).add(value); }
        else                 { newItem(obj, lang).add(value);          }
    }

    private List<Object> newItem(DBObject obj, String keyword)
    {
        List list = (List)obj.get(keyword);
        if ( list == null )
        {
            list = new ArrayList(1);
            obj.put(keyword, list);
        }
        return list;
    }
}
