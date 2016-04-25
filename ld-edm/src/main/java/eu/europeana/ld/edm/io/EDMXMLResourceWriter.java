package eu.europeana.ld.edm.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class EDMXMLResourceWriter implements Closeable
{
    private static String   SEPARATOR = "  ";

    private PrintStream _ps    = null;
    private boolean     _first = true;

    public EDMXMLResourceWriter(PrintStream ps) { _ps = ps; }

    public void write(Resource r) throws IOException
    {
        if ( _first ) { writeStart(r.getModel()); _first = false; }
        writeResource(r);
        _ps.flush();
    }

    public void close() throws IOException
    {
        writeEnd();
    }


    private Resource getType(Resource rsrc)
    {
        Statement stmt = rsrc.getProperty(RDF.type);
        return (stmt == null ? null : stmt.getObject().asResource());
    }

    private boolean isType(Statement stmt, Resource type)
    {
        Property predicate = stmt.getPredicate();
        return ( (predicate.getURI().equals(RDF.type.getURI()))
              && (type.equals(stmt.getObject())) );
    }

    private String getQName(Resource rsrc)
    {
        return rsrc.getModel().getNsURIPrefix(rsrc.getNameSpace())
             + ":" + rsrc.getLocalName();
    }

    private String getURI(Resource rsrc)
    {
        return StringEscapeUtils.escapeXml(rsrc.getURI());
    }

    private void writeResource(Resource rsrc)
    {
        Resource type = getType(rsrc);
        if ( type == null ) { return; }

        String name = getQName(type);
        _ps.println(SEPARATOR + "<" + name + " rdf:about=\""
                  + getURI(rsrc) + "\">");
        StmtIterator iter = rsrc.listProperties();
        while ( iter.hasNext() )
        {
            Statement stmt = iter.next();
            if ( isType(stmt, type) ) { continue; }

            writeStatement(stmt);
        }
        _ps.println(SEPARATOR + "</" + name + ">");
    }

    private void writeStatement(Statement stmt)
    {
        String             name  = getQName(stmt.getPredicate());
        RDFNode node = stmt.getObject();
        if ( node.isLiteral() ) { 
            writeLiteralProperty(name, node.asLiteral());
            return;
        }
        writeResourceProperty(name, node.asResource());
    }

    private void writeResourceProperty(String name, Resource r)
    {
        writeProperty(name
                    , Collections.singletonMap("rdf:resource", getURI(r))
                    , null);
    }

    private void writeLiteralProperty(String name, Literal l)
    {
        Map<String,String> attrs = null;
        String value = l.getString();
        String lang = l.getLanguage();
        if ( !lang.isEmpty()  ) { attrs = Collections.singletonMap("xml:lang", lang); }

        String datatype = l.getDatatypeURI();
        if ( datatype != null
          && !datatype.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")) { attrs = Collections.singletonMap("rdf:datatype", datatype); }
        writeProperty(name, attrs, value);
    }

    private void writeProperty(String label, Map<String,String> attrs
                             , String value)
    {
        _ps.print(SEPARATOR);
        _ps.print(SEPARATOR);
        _ps.print("<");
        _ps.print(label);
        if ( attrs != null )
        {
            for (String key : attrs.keySet())
            {
                _ps.print(' '); _ps.print(key); _ps.print("=\"");
                _ps.print(attrs.get(key)); _ps.print('"');
            }
        }
        if ( value != null )
        {
            _ps.print(">");
            _ps.print(StringEscapeUtils.escapeXml(value));
            _ps.print("</");
            _ps.print(label);
        }
        else { _ps.print('/'); }
        _ps.println(">");
    }

    private void writeStart(Model m)
    {
        _ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        _ps.print("<rdf:RDF");
        boolean first = true;
        for ( Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet() )
        {
            if ( !first ) { _ps.print("\n        "); }
            _ps.print(" xmlns:");
            _ps.print(entry.getKey());
            _ps.print("=\"");
            _ps.print(entry.getValue());
            _ps.print("\"");
            first = false;
        }
        _ps.println(">");
    }

    private void writeEnd() { _ps.println("</rdf:RDF>"); }
}