/**
 * 
 */
package eu.europeana.ld.skos;

import java.net.URLEncoder;

import org.apache.jena.iri.IRI;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;


/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Sep 2015
 */
public class SKOSExtractor
{
    private static Property[] PROPERTIES = { DC.subject };

    private String     _base;
    private Property[] _props;

    public SKOSExtractor(String base, Property... props)
    {
        _props = props;
        _base  = base;
    }

    public SKOSExtractor(String base) { this(base, PROPERTIES); }


    public void extract(Model src, Model trg)
    {
        trg.setNsPrefix("skos", SKOS.uri);

        Resource scheme = trg.createResource(_base + "ConceptScheme");
        scheme.addProperty(RDF.type, SKOS.ConceptScheme);

        for ( Property p : _props ) { createConcepts(scheme, p, src); }
    }

    private void createConcepts(Resource scheme, Property p, Model src)
    {
        StmtIterator iter = src.listStatements(null, p, (RDFNode)null);
        try {
            while ( iter.hasNext() )
            {
                Statement stmt = iter.nextStatement();
                RDFNode   node = stmt.getObject();
                if ( node.isResource() ) { continue; }

                Literal  l    = node.asLiteral();
                Resource rsrc = scheme.getModel().createResource(createURI(l));
                rsrc.addProperty(RDF.type      , SKOS.Concept);
                rsrc.addProperty(SKOS.prefLabel, l.getString(), l.getLanguage());
              //rsrc.addProperty(SKOS.note     , stmt.getSubject().getURI());
                rsrc.addProperty(SKOS.note     , stmt.getSubject());
                rsrc.addProperty(SKOS.inScheme , scheme);
            }
        }
        finally { iter.close(); }
    }

    private String createURI(Literal l)
    {
        return _base + URLEncoder.encode(l.getString());
    }
}