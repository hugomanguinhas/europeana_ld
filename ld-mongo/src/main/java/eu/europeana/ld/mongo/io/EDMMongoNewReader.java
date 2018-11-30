/**
 * 
 */
package eu.europeana.ld.mongo.io;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.bson.Document;

import com.mongodb.BasicDBObject;

import eu.europeana.ld.json.RewriteSupport;
import static eu.europeana.ld.mongo.io.EDMMongoConstants.*;
import static eu.europeana.ld.edm.EDM.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 3 Jan 2017
 */
public class EDMMongoNewReader
{
    private Map<String,String> _rewrites;

    public EDMMongoNewReader(String url) throws IOException
    {
        _rewrites = new RewriteSupport().getRewrites(url);
        _rewrites.put("depiction","http://xmlns.com/foaf/0.1/depiction");
    }

    public Resource parseResource(Document doc)
    {
        return parseResource(doc
                           , setPrefixes(ModelFactory.createDefaultModel()));
    }

    public Resource parseResource(Map<String, Object> doc, Model m)
    {
        Resource r = createResource(doc, m);
        if ( r == null ) { return null; }

        parseType(doc, r);
        for ( String key : doc.keySet() )
        {
            if ( key.equals(FIELD_ID) || key.equals(FIELD_TYPE) ) { continue; }

            String   pURI = _rewrites.get(key);
            if ( pURI == null ) { logMissing(key); continue; }

            parseProperty(r, m.getProperty(pURI), doc.get(key));
        }
        return r;
    }

    private void logMissing(String uri)
    {
        System.err.println("Unsupported field: " + uri);
    }

    /***************************************************************************
     * Private Methods - Values
     **************************************************************************/

    private Resource createResource(Map<String, Object> doc, Model m)
    {
        if ( doc == null ) { return null; }

        BasicDBObject o;
        Object id = doc.get(FIELD_ID);
        return ( id == null || !(id instanceof String) )
               ? null
               : m.getResource((String)id);
    }

    private void parseType(Map<String, Object> doc, Resource r)
    {
        Object type = doc.get(FIELD_TYPE);
        if ( type == null || !(type instanceof String) ) { return; }

        if ( type.equals("skos:Concept") ) { r.addProperty(RDF.type, SKOS.Concept); return; }

        String newType = _rewrites.get((String)type);
        newType = (newType == null ? (String)type : newType);
        r.addProperty(RDF.type, r.getModel().getResource(newType));
    }

    private void parseProperty(Resource r, Property p, Object o)
    {
        if ( !(o instanceof Map) ) { return; }

        Map<String, Object> doc = (Map<String, Object>)o;
        for ( String key : doc.keySet() )
        {
            Object        v = doc.get(key);
            ParserContext c = null;
                 if ( key.equals(FIELD_LITERAL ) ) { c = new ContextLiteral(r,p); }
            else if ( key.equals(FIELD_RESOURCE) ) { c = new ContextResource(r,p);}
            else                                   { c = new ContextLangLiteral(r, p, key); }

            parseValue(v, c);
        }
    }

    private void parseValue(Object o, ParserContext ctxt)
    {
        if ( o == null             ) { return; }
        if ( o instanceof String   ) { ctxt.newStatement((String)o); return;  }
        if ( o instanceof List     ) { parseArray((List)o, ctxt);    return;  }
      //if ( o instanceof Document ) { parseDoc((Document)o, ctxt);  return;  }
    }

    private void parseArray(List list, ParserContext ctxt)
    {
        for ( Object o : list ) { parseValue(o, ctxt); }
    }

    /***************************************************************************
     * Private Class - ParserContext
     **************************************************************************/

    protected abstract class ParserContext
    {
        protected Resource _resource;
        protected Property _property;

        public ParserContext(Resource r, Property p)
        { 
            _resource = r;
            _property = p;
        }

        public abstract void newStatement(String v);
    }

    protected class ContextResource extends ParserContext
    {
        public ContextResource(Resource r, Property p)
        {
            super(r,p);
        }

        @Override
        public void newStatement(String v)
        {
            _resource.addProperty(_property
                                , _resource.getModel().getResource(v));
        }
    }

    protected class ContextLiteral extends ParserContext
    {
        public ContextLiteral(Resource r, Property p)
        {
            super(r,p);
        }

        @Override
        public void newStatement(String v)
        {
            _resource.addLiteral(_property
                               , _resource.getModel().createLiteral(v));
        }
    }

    protected class ContextLangLiteral extends ParserContext
    {
        protected String _lang;

        public ContextLangLiteral(Resource r, Property p, String lang)
        {
            super(r,p);
            _lang = lang;
        }

        @Override
        public void newStatement(String v)
        {
            _resource.addLiteral(_property, _resource.getModel().createLiteral(v, _lang));
        }
    }
}
