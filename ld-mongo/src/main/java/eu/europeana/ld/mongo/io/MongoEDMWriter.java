/**
 * 
 */
package eu.europeana.ld.mongo.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import eu.europeana.ld.edm.CC;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.edm.ORE;
import eu.europeana.ld.edm.SVCS;
import eu.europeana.ld.mongo.MongoClassDef;
import static eu.europeana.ld.mongo.MongoClassDef.*;
import static eu.europeana.ld.mongo.MongoEDMConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Jun 2017
 */
public class MongoEDMWriter
{
    private static Map<Resource,String> _groupings = new HashMap();

    static
    {
        _groupings.put(ORE.Aggregation         , AGGREGATIONS);
        _groupings.put(EDM.EuropeanaAggregation, EUROPEANA_AGGREGATION);
        _groupings.put(ORE.Proxy               , PROXIES);
        _groupings.put(EDM.ProvidedCHO         , PROVIDED_CHOS);
        _groupings.put(SKOS.Concept            , CONCEPTS);
        _groupings.put(EDM.Place               , PLACES);
        _groupings.put(EDM.Agent               , AGENTS);
        _groupings.put(EDM.TimeSpan            , TIMESPANS);
        _groupings.put(CC.License              , LICENSES);
        _groupings.put(SVCS.Service            , SERVICES);
        _groupings.put(EDM.WebResource         , WEB_RESOURCES);
    }

    public Map<String,Object> writeModel(Model m, Map<String,Object> doc)
    {
        for ( MongoClassDef def : getDefinitions() )
        {
            Object obj = writeResources(m, def);
            if ( obj == null ) { continue; }

            String grouping = _groupings.get(def.getType());
            
            doc.put(grouping, obj);
        }

        return doc;
    }

    private List<Object> writeResources(Model m, MongoClassDef def)
    {
        ResIterator iter = m.listResourcesWithProperty(RDF.type, def.getType());
        if ( !iter.hasNext() ) { return null; }

        List<Object> list = new ArrayList();
        while ( iter.hasNext() )
        {
            list.add(writeResource(iter.nextResource(), new Document()));
        }
        return list;
    }

    public Document writeResource(Resource r, Document doc)
    {
        MongoClassDef def = getClassDefinition(r);
        if ( def == null ) { return doc; }

        doc.put(ABOUT, r.getURI());
        for ( Property p : getProperties(r) )
        {
            writeProperty(r, def.get(p), doc);
        }
        return doc;
    }

    protected MongoClassDef getClassDefinition(Resource r)
    {
        Statement stmt = r.getRequiredProperty(RDF.type);
        return ( stmt == null ? null : getDefinition(stmt.getResource()) );
    }

    private Collection<Property> getProperties(Resource r)
    {
        Collection<Property> col = new HashSet();
        StmtIterator iter = r.listProperties();
        while ( iter.hasNext() ) { col.add(iter.next().getPredicate()); }
      //col.remove(EDM.type);
        return col;
    }

    private void writeProperty(Resource r, PropertyDef def, Document doc)
    {
        Object obj = createProperty(r, def);
        if ( obj == null ) { return; }

        doc.put(def.getPropertyLabel(), obj);
    }

    protected Object createProperty(Resource r, PropertyDef def)
    {
        if ( def == null ) { return null; }

        StmtIterator iter = r.listProperties(def.getProperty());
        try {
            switch ( def.getJsonType() )
            {
                case ARRAY  : return createPropertyAsArray(iter);
                case VALUE  : return createPropertyAsValue(iter);
                case BOOLEAN: return createPropertyAsBoolean(iter);
                case MAP    : return createPropertyAsMap(iter);
            }
        }
        finally { iter.close(); }

        return null;
    }

    private Object createPropertyAsBoolean(StmtIterator iter)
    {
        return Boolean.parseBoolean((String)createPropertyAsValue(iter));
    }

    private Object createPropertyAsValue(StmtIterator iter)
    {
        return (iter.hasNext() ? getValue(iter.next().getObject()) : null );
    }

    private ArrayList createPropertyAsArray(StmtIterator iter)
    {
        ArrayList list = new ArrayList();
        while (iter.hasNext())
        {
            Object value = getValue(iter.next().getObject());
            if ( value == null ) { continue; }

            list.add(value);
        }
        return list;
    }

    private DBObject createPropertyAsMap(StmtIterator iter)
    {
        if ( !iter.hasNext() ) { return null; }

        DBObject obj = new BasicDBObject();
        while (iter.hasNext())
        {
            RDFNode node = iter.next().getObject();
            if ( node.isURIResource() )
            { 
                getDefault(obj).add(node.asResource().getURI());
                continue;
            }

            if ( !node.isLiteral() ) { continue; }

            getLangKey(obj, node.asLiteral().getLanguage()).add(getValue(node));
        }
        return obj;
    }

    private ArrayList getDefault(DBObject obj)
    {
        return getMapKey(obj, "def");
    }

    private ArrayList getMapKey(DBObject obj, String key)
    {
        ArrayList def = (ArrayList)obj.get(key);
        if ( def != null ) { return def; }

        def = new ArrayList(1);
        obj.put(key, def);
        return def;
    }

    private ArrayList getLangKey(DBObject obj, String lang)
    {
        if ( lang == null || lang.trim().isEmpty() ) { return getDefault(obj); }
        return getMapKey(obj, lang.trim());
    }

    private Object getValue(RDFNode node)
    {
        if ( node.isLiteral()     ) { return node.asLiteral().getValue(); }
        if ( node.isURIResource() ) { return node.asResource().getURI();  }

        return null;
    }
}
