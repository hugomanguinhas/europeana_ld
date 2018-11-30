package eu.europeana.ld.edm.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.RecordWriter;
import static eu.europeana.ld.jena.JenaUtils.*;

public class XMLRecordWriter implements RecordWriter
{
    private static String   SEPARATOR = "  ";

    private Collection<Resource> _classes;

    public XMLRecordWriter() { this(EDM.CLASSES); }

    public XMLRecordWriter(Resource... classes)
    {
        _classes = Arrays.asList(classes);
    }


    /***************************************************************************
     * Interface ModelWriter
     **************************************************************************/

    @Override
    public void write(Resource r, OutputStream out) throws IOException
    {
        write(r, new PrintStream(out, false, "UTF-8"));
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private void write(Resource r, PrintStream out) throws IOException
    {
        Model m = r.getModel();
        writeStart(m, out);
        List<Resource> list = m.listSubjects().toList();
        Collections.sort(list, RESOURCE_COMPARATOR);
        for ( Resource rsrc : list ) { writeResource(rsrc, out); }
        writeEnd(out);
        out.flush();
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

    private boolean isToWrite(Resource type)
    {
        if ( type == null ) { return false; }

        String stype = type.getURI();
        for ( Resource r : _classes )
        {
            if ( r.getURI().equals(stype) ) { return true; }
        }
        return false;
    }

    private void writeResource(Resource rsrc, PrintStream out)
    {
        Resource type = getType(rsrc);
        if ( !isToWrite(type) ) { return; }

        String name = getQName(type);
        out.println(SEPARATOR + "<" + name + " rdf:about=\"" + getURI(rsrc) + "\">");
        StmtIterator iter = rsrc.listProperties();
        while ( iter.hasNext() )
        {
            Statement stmt = iter.next();
            if ( isType(stmt, type) ) { continue; }

            writeStatement(stmt, out);
        }
        out.println(SEPARATOR + "</" + name + ">");
    }

    private void writeStatement(Statement stmt, PrintStream out)
    {
        String             name  = getQName(stmt.getPredicate());
        Map<String,String> attrs = null;
        String             value = null;
        RDFNode node = stmt.getObject();

        if ( node.isResource() )
        {
            attrs = Collections.singletonMap("rdf:resource"
                                           , getURI(node.asResource()));
            writeProperty(name, attrs, value, out);
            return;
        }

        if ( !node.isLiteral() ) { return; }

        Literal l = node.asLiteral();
        value = l.getString();

        String lang = l.getLanguage();
        if ( !lang.isEmpty() )
        {
            attrs = Collections.singletonMap("xml:lang", lang);
            writeProperty(name, attrs, value, out);
            return;
        }

        RDFDatatype dt = l.getDatatype();
        if ( hasDatatype(dt) ) {
            attrs = Collections.singletonMap("rdf:datatype", dt.getURI());
        }
        writeProperty(name, attrs, value, out);
    }

    private boolean hasDatatype(RDFDatatype dt)
    {
        return ( (dt != null)
               && !dt.getURI().equals("http://www.w3.org/2001/XMLSchema#string"));
    }

    private void writeProperty(String label, Map<String,String> attrs
                             , String value, PrintStream out)
    {
        out.print(SEPARATOR);
        out.print(SEPARATOR);
        out.print("<");
        out.print(label);
        if ( attrs != null )
        {
            for (String key : attrs.keySet())
            {
                out.print(' '); out.print(key); out.print("=\"");
                out.print(attrs.get(key)); out.print('"');
            }
        }
        if ( value != null )
        {
            out.print(">");
            out.print(StringEscapeUtils.escapeXml(value));
            out.print("</");
            out.print(label);
        }
        else { out.print('/'); }
        out.println(">");
    }

    private void writeStart(Model m, PrintStream out)
    {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        out.print("<rdf:RDF");
        for ( Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet() )
        {
            out.print(" xmlns:");
            out.print(entry.getKey());
            out.print("=\"");
            out.print(entry.getValue());
            out.print("\"");
        }
        out.println(">");
    }

    private void writeEnd(PrintStream out) { out.println("</rdf:RDF>"); }
}