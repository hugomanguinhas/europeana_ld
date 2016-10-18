/**
 * 
 */
package eu.europeana.ld.mongo;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.log4j.Logger;
import org.bson.Document;

import eu.europeana.ld.harvester.LDHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public class MongoEntityParser extends MongoEDMParser
{
    private static Logger _log = Logger.getLogger(LDHarvester.class);
    private static List<String> _filter = Arrays.asList("id", "about");

    public MongoEntityParser() { super(); }


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public Resource parse(Document doc, Model model)
    {
        String uri = doc.getString("codeUri");
        if ( uri == null ) { return null; }

        Resource resource = model.getResource(uri);

        String type = doc.getString("entityType");
        MongoClassDef def = MongoClassDef.getDefinition(type);
        if ( def == null ) {
            _log.error("Unknown entity: " + type);
            return null;
        }
        resource.addProperty(RDF.type, def.getType());

        Object o = doc.get("representation");
        parseEntity((Document)o, new ParserContext(resource, def));
        return resource;
    }


    /***************************************************************************
     * Protected Methods
     **************************************************************************/

    @Override
    protected boolean filter(String property)
    {
        return _filter.contains(property);
    }
}
