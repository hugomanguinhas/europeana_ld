package eu.europeana.ld.edm.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.jena.ModelWriter;
import eu.europeana.ld.jena.StatementComparator;
import static eu.europeana.ld.jena.JenaUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

//https://github.com/apache/jena/blob/master/jena-arq/src/main/java/org/apache/jena/riot/out/NodeFormatterTTL.java
public class EDMTurtleWriter
{
    private static String   SEPARATOR = "  ";

    private OutputStream       _out;
    private BufferedWriter     _ps;
    private Map<String,String> _decl = new HashMap();

    public EDMTurtleWriter() {}


    /***************************************************************************
     * Public Methods
     **************************************************************************/

    public void start(File file) throws IOException
    {
        String name = file.getName();
        FileOutputStream fos = new FileOutputStream(file);
        if ( name.endsWith(".ttl") ) { start(fos); return; }
        if ( name.endsWith(".gz" ) )
        {
            start(new GZIPOutputStream(fos, 65536));
            return;
        }
        if ( name.endsWith(".zip") )
        {
            ZipOutputStream zos   = new ZipOutputStream(fos, Charset.forName("UTF-8"));
            String          zname = name.substring(0, name.length()-4) + ".ttl";
            zos.putNextEntry(new ZipEntry(zname));
            start(zos);
        }
    }

    public void start(OutputStream out) throws IOException
    {
        _out = out;
        _ps  = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"), 1024);
        _decl.clear();
    }

    public void finish() throws IOException
    {
        try {
            _ps.flush();
            if ( _out instanceof ZipOutputStream )
            {
                ((ZipOutputStream)_out).closeEntry();
            }
        }
        finally {
            IOUtils.closeQuietly(_ps);
            _decl.clear();
            _ps  = null;
            _out = null;
        }
    }

    public void write(Model m) throws IOException
    {
        writeHeader(m);
        ResIterator iter = m.listSubjectsWithProperty(RDF.type);
        try {
            while ( iter.hasNext() ) { writeResource(iter.next()); }
        }
        finally { iter.close(); }
    }

    public void write(Resource r) throws IOException
    {
        writeHeader(r.getModel());
        writeResource(r);
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private void writeResource(Resource r) throws IOException
    {
        try {
            Map<String,Property> props = getProperties(r);
            int                  len   = calcMaxLength(props.keySet());
    
            boolean first = true;
            _ps.newLine();
            writeValue(r);
            for ( Map.Entry<String, Property> entry : props.entrySet() )
            {
                if ( first ) { first = false;    }
                else         { _ps.append(" ;"); }
                _ps.newLine();
    
                writePropertyDecl(entry.getKey(), len);
                writePropertyValues(r, entry.getValue());
            }
            _ps.append(" .");
            _ps.newLine();
        }
        catch (IOException e) { throw e; }
        catch (Throwable   t) {
            throw new IOException("Unexpected exception while writting: "
                                + r.getURI(), t);
        }
    }

    private void writeHeader(Model m) throws IOException
    {
        for ( Map.Entry<String, String> entry : m.getNsPrefixMap().entrySet() )
        {
            String nPre = entry.getKey();
            String nURI = entry.getValue();
            String oURI = _decl.get(nPre);
            if (oURI == null || !oURI.equals(nURI)) { writePrefix(nPre, nURI); }
        }
    }

    private void writePrefix(String prefix, String ns) throws IOException
    {
        _decl.put(prefix, ns);
        _ps.append("@prefix ").append(prefix).append(": ");
        writeAsIRI(ns).append(" .");
        _ps.newLine();
    }

    private void writePropertyDecl(String qname, int length) throws IOException
    {
        _ps.append("\t").append(qname);
        for ( int i = length - qname.length(); i > 0; i-- ) { _ps.append(" "); }
        _ps.append("  ");
    }

    private void writePropertyValues(Resource r, Property p) throws IOException
    {
        boolean first = true;
        StmtIterator iter = r.listProperties(p);
        try {
            while (iter.hasNext())
            {
                if ( first ) { first = false;    }
                else         { _ps.append(", "); }
                writeValue(iter.next().getObject());
            }
        }
        finally { iter.close(); }
    }

    private void writeValue(RDFNode node) throws IOException
    {
        if ( node.isResource() ) { writeValue(node.asResource()); }
        else                     { writeValue(node.asLiteral());  }
    }

    private void writeValue(Resource r) throws IOException
    {
        String uri  = r.getURI();
        for ( Map.Entry<String, String> entry : _decl.entrySet() )
        {
            String value = entry.getValue();
            if ( !uri.startsWith(value) ) { continue; }

            _ps.append(entry.getKey() + ":" + uri.substring(value.length()));
            return;
        }

        writeAsIRI(uri);
    }

    private void writeValue(Literal l) throws IOException
    {
        _ps.append('"');
        writeAsString(l.getString());
        _ps.append('"');

        String lang = l.getLanguage();
        if ( !isEmpty(lang)  ) { _ps.append("@").append(lang); return; }

        RDFDatatype dt = l.getDatatype();
        if ( hasDatatype(dt) ) { writeDatatype(dt, l.getModel()); }
    }

    /***************************************************************************
     * Private Methods - Escaping
     **************************************************************************/

    private void escapeUnicode(char c) throws IOException
    {
        _ps.append("\\u");
        String s = Integer.toHexString(c).toUpperCase();
        for ( int i = 4 - s.length(); i > 0; i--) { _ps.append('0'); }
        _ps.append(s);
    }

    private BufferedWriter writeAsString(String str) throws IOException
    {
        int len = str.length();
        for ( int i = 0; i < len; i++ )
        {
            char c = str.charAt(i);
            switch (c) {
                case '\t': 
                case '\b': 
                case '\n': 
                case '\r': 
                case '\f': 
                case '\"': 
                case '\\': _ps.append('\\').append(c); continue;
            }
            _ps.append(c);
        }
        return _ps;
    }

    private BufferedWriter writeDatatype(RDFDatatype dt
                                       , Model model) throws IOException
    {
        _ps.append("^^");
        String uri    = dt.getURI();
        String prefix = model.getNsURIPrefix(XSD.NS);
        if ( prefix != null && uri.startsWith(XSD.NS) )
        {
            _ps.append(prefix).append(":")
               .append(uri.substring(XSD.NS.length()));
            return _ps;
        }
        return writeAsIRI(uri);
    }

    private BufferedWriter writeAsIRI(String str) throws IOException
    {
        _ps.append('<');
        int len = str.length();
        for ( int i = 0; i < len; i++ )
        {
            char c = str.charAt(i);
            if ( c >= '\u0000' && c <= '\u0020' ) { escapeUnicode(c); continue;}

            switch (c) {
                case '<':
                case '>':
                case '"':
                case '{':
                case '}':
                case '|':
                case '^':
                case '`':
                case '\\': escapeUnicode(c); continue;
            }
            _ps.append(c);
        }
        _ps.append('>');
        return _ps;
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private Map<String,Property> getProperties(Resource r)
    {
        Map<String,Property> ret = new TreeMap<String,Property>();
        StmtIterator iter = r.listProperties();
        try {
            while ( iter.hasNext() )
            {
                Property p = iter.next().getPredicate();
                ret.put(RDF.type.equals(p) ? "a" : getQName(p), p);
            }
        }
        finally { iter.close(); }
        return ret;
    }

    private int calcMaxLength(Collection<String> col)
    {
        int len = 0;
        for ( String value : col ) { len = Math.max(len, value.length()); }
        return len;
    }

    private boolean hasDatatype(RDFDatatype dt)
    {
        return ( (dt != null)
               && !dt.getURI().equals("http://www.w3.org/2001/XMLSchema#string"));
    }

    public static void main(String[] args) throws IOException
    {
      //File src = new File("D:\\work\\data\\virtuoso\\virtuoso_test.ttl");
      //File dst = new File("D:\\work\\data\\virtuoso\\virtuoso_test_new.ttl");
        File src = new File("D:\\work\\data\\dump\\44_201-EDM.ttl");
        File dst = new File("D:\\work\\data\\dump\\44_201-EDM_new.ttl");

        Model m = ModelFactory.createDefaultModel();
        m.read(new FileReader(src), null, "TTL");

        EDMTurtleWriter writer = new EDMTurtleWriter();
        writer.start(dst);
        try     { writer.write(m); }
        finally { writer.finish(); }
    }
}