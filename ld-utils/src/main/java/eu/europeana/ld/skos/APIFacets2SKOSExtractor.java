/**
 * 
 */
package eu.europeana.ld.skos;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Sep 2015
 */
public class APIFacets2SKOSExtractor implements SKOSExtractor<InputStream>
{
    private static String DEF_PATH 
        = "/response/lst[@name='facet_counts']/lst[@name='facet_fields']"
        + "/lst/int/@name";

    private String          _base;
    private XPathExpression _expr;

    public APIFacets2SKOSExtractor(String base, String path)
    {
        _base = base;
        try {
            _expr = XPathFactory.newInstance().newXPath().compile(path);
        }
        catch (XPathExpressionException e) { e.printStackTrace(); }
    }

    public APIFacets2SKOSExtractor(String base) { this(base, DEF_PATH); }


    public void extract(InputStream src, Model trg)
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false);

        Document doc = null;
        try
        {
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            doc = builder.parse(src);
        }
        catch (ParserConfigurationException
             | SAXException
             | IOException e) { e.printStackTrace(); return; }

        NodeList nodes = null;
        try
        {
            nodes = (NodeList)_expr.evaluate(doc, XPathConstants.NODESET);
        }
        catch (XPathExpressionException e) { e.printStackTrace(); return; }

        trg.setNsPrefix("skos", SKOS.uri);

        Resource scheme = trg.createResource(_base + "ConceptScheme");
        scheme.addProperty(RDF.type, SKOS.ConceptScheme);

        int iL = nodes.getLength();
        for ( int i = 0; i < iL; i++)
        {
            Attr   attr  = (Attr)nodes.item(i);
            String value = attr.getValue().trim();
            if ( !value.isEmpty() ) { createConcept(scheme, value); }
        }
    }

    private Resource createConcept(Resource scheme, String label)
    {
        label = label.replaceAll("[ ]+", " ");
        String iri = SKOSUtils.toIRI(_base, label);
        Resource rsrc = scheme.getModel().createResource(iri);
        rsrc.addProperty(RDF.type      , SKOS.Concept);
        rsrc.addProperty(SKOS.prefLabel, label);
        rsrc.addProperty(SKOS.inScheme , scheme);
        return rsrc;
    }
}