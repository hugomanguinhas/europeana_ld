/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.bson.Document;

import eu.europeana.ld.edm.EBUCORE;
import eu.europeana.ld.harvester.LDHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public class MongoEDMParser
{
    private static Logger _log = Logger.getLogger(LDHarvester.class);

    private static List<String> _filter = Arrays.asList("_id", "className"
                                                      , "about"
                                                      , "edmPreviewNoDistribute"
                                                      , "webResources"
                                                      , "fileFormat");
    private static String DATA_NS = "http://data.europeana.eu";

    protected Properties _props;

    public MongoEDMParser() {}


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public Resource parse(Document doc, Model model)
    {
        String uri = doc.getString("about");
        if ( uri == null ) {
            _log.error("Missing uri for <" + doc.getString("_id")
                     + "> of type <" + doc.getString("className") + ">");
            return null;
        }

        if ( uri.startsWith("/") ) { uri = DATA_NS + uri; }

        Resource resource = (uri == null ? model.createResource()
                                         : model.getResource(uri));

        String        type = doc.getString("className");
        MongoClassDef def  = MongoClassDef.getDefinition(type);
        if ( def == null ) {
            _log.error("Unknown entity: " + type); return null;
        }
        resource.addProperty(RDF.type, def.getType());

        parseEntity(doc, new ParserContext(resource, def));

        return resource;
    }


    /***************************************************************************
     * Protected Methods
     **************************************************************************/

    protected boolean filter(String property)
    {
        return _filter.contains(property);
    }

    protected void parseEntity(Document doc, ParserContext ctxt)
    {
        MongoClassDef def   = ctxt.getDefinition();
        Model         model = ctxt.getModel();
        def.setPrefixes(model);

        for ( String key : doc.keySet() )
        {
            if ( filter(key) ) { continue; }

            Property p = def.get(key);
            if ( p == null ) { 
                _log.error("Unsupported label: " + key
                         + " for definition: " + def.getType().getLocalName());
                continue;
            }

            ctxt.setProperty(p);
            parse(doc.get(key), ctxt);
        }
    }


    /***************************************************************************
     * Private Methods - Values
     **************************************************************************/

    private void parse(Object o, ParserContext ctxt)
    {
        if ( o == null             ) { return; }
        if ( o instanceof String   ) { parseString((String)o, ctxt); return;  }
        if ( o instanceof Boolean  ) { ctxt.newBoolean((Boolean)o);  return;  }
        if ( o instanceof Number   ) { ctxt.newNumber((Number)o);    return;  }
        if ( o instanceof List     ) { parseArray((List)o, ctxt);    return;  }
        if ( o instanceof Document ) { parseDoc((Document)o, ctxt);  return;  }

        System.err.println("Unknown object: " + o + " of type: " + o.getClass());
    }

    private void parseDoc(Document d, ParserContext ctxt)
    {
        if ( isSingleton(d) ) { parseSingleton(d, ctxt); return; }
        parseLanguageMap(d, ctxt);
    }

    private void parseString(String str, ParserContext ctxt)
    {
        if ( str.trim().isEmpty() ) { return; }

             if ( isRelativeResource(str) ) { ctxt.newResource(DATA_NS + str); }
        else if ( isResource(str)         ) { ctxt.newResource(str);           }
        else                                { ctxt.newLiteral(str);            }
    }

    private void parseArray(List list, ParserContext ctxt)
    {
        for ( Object o : list ) { parse(o, ctxt); }
    }

    private void parseSingleton(Document doc, ParserContext ctxt)
    {
        parse(doc.get("def"), ctxt);
    }

    private void parseLanguageMap(Document doc, ParserContext ctxt)
    {
        for ( String lang : doc.keySet() )
        {
            ctxt.setLang(lang);
            parse(doc.get(lang), ctxt);
        }
        ctxt.setLang(null);
    }

    private boolean isRelativeResource(String uri)
    {
        return (uri.startsWith("/aggregation/")
             || uri.startsWith("/item/")
             || uri.startsWith("/proxy/"));
    }

    private boolean isResource(String uri)
    {
        return (uri.startsWith("http://") || uri.startsWith("https://"));
    }

    private boolean isSingleton(Document doc)
    {
        return doc.containsKey("def");
    }


    /***************************************************************************
     * Private Class - ParserContext
     **************************************************************************/

    protected class ParserContext
    {
        private MongoClassDef  _def;
        private Resource       _resource;
        private Property       _property;
        private String         _lang;

        public ParserContext(Resource r, MongoClassDef def)
        {
            _resource = r;
            _def      = def;
        }

        public Model getModel()              { return _resource.getModel(); }
        public MongoClassDef getDefinition() { return _def;   }

        public void setProperty(Property p) { _property = p; }
        public void setLang(String lang)    { _lang = lang;  }

        public void newLiteral(String str)
        {
            if ( _property == EBUCORE.orientation ) { str = str.toLowerCase(); }

            Model m = _resource.getModel();
            if ( _lang != null ) {
                _resource.addProperty(_property
                                    , m.createLiteral(str, _lang));
                return;
            }

            RDFDatatype type = MongoClassDef.getDatatype(_property);
            Literal     l    = type == null ? m.createLiteral(str)
                                            : m.createTypedLiteral(str, type);
            _resource.addProperty(_property, l);
        }

        public void newBoolean(Boolean b)
        {
            if ( b == null ) { return; }

            Model model = _resource.getModel();
            _resource.addProperty(_property
                                , model.createLiteral(b ? "true" : "false"));
        }

        public void newNumber(Number n)
        {
            if ( n == null ) { return; }

            Model model = _resource.getModel();
            RDFDatatype type = MongoClassDef.getDatatype(_property);
            Literal     l    = type == null ? model.createLiteral(n.toString())
                                            : model.createTypedLiteral(n, type);
            _resource.addProperty(_property, l);
        }

        public void newResource(String iri)
        {
            if ( iri == null ) { return; }

            Model model = _resource.getModel();
            _resource.addProperty(_property, model.getResource(iri));
        }
    }
}
