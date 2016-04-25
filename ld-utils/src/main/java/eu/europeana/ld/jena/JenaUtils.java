package eu.europeana.ld.jena;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;


import org.apache.jena.util.FileUtils;

//import eu.europeana.edm.io.EDMXMLWriter;
import static org.apache.commons.io.IOUtils.*;

public class JenaUtils
{
    public static ResourceComparator RESOURCE_COMPARATOR
        = new ResourceComparator();

    public static void clearAll(Collection<Resource> col)
    {
        for ( Resource rsrc : col ) { rsrc.removeProperties(); }
    }

    public static String getQName(Resource rsrc)
    {
        String p = rsrc.getModel().getNsURIPrefix(rsrc.getNameSpace());
        return ( p == null ? rsrc.getURI() : (p + ":" + rsrc.getLocalName()) );
    }

    public static Model load(File file)
    {
        return load(file, ModelFactory.createDefaultModel());
    }

    public static Model load(File file, Model model)
    {
        return model.read(file.getAbsolutePath());
/*
        try {
            m.read(new FileReader(file), null, "RDF/XML");
        } catch (IOException e) {
            System.out.println("Could not read file: " + file.getName()
                             + ", reason:" + e.getMessage());
        }
*/
    }

    public static Model loadAll(File dir, String ext)
    {
        return loadAll(dir, ext, ModelFactory.createDefaultModel());
    }

    public static Model loadAll(File dir, String ext, Model m)
    {
        for ( File file : dir.listFiles() )
        {
            if ( file.isDirectory() ) { loadAll(file, ext, m); continue; }

            String fext = FileUtils.getFilenameExt(file.getName());
            if ( (ext != null) && ext.equals(fext)) { load(file, m); }
        }
        return m;
    }

    public static Model loadAll(File dir)
    {
        return loadAll(dir, ModelFactory.createDefaultModel());
    }

    public static Model loadAll(File dir, Model m)
    {
        for ( File file : dir.listFiles() )
        {
            if ( file.isDirectory() ) { loadAll(file, m); continue; }

            load(file, m);
        }
        return m;
    }

    /*
    public static Model loadModel(File file)
    {
        Lang lang = RDFLanguages.filenameToLang(file.getName());
        if ( lang == null ) { return null; }

    }
    */

    public static void store(Model model, File dst) throws IOException
    {
        Lang lang = RDFLanguages.filenameToLang(dst.getName());
        if ( lang == null ) { return; }

        store(model, new FileOutputStream(dst), lang.getLabel());
    }

    public static void store(Model model, OutputStream out, String lang) 
           throws IOException
    {
        try {
            model.write(out, lang);
            out.flush();
        }
        catch (IOException e)
        {
            //logError("Error writing to file: " + dst.getAbsolutePath(), e);
        }
        finally { closeQuietly(out); }
    }

    public static void store(Model model, File dest, String sFormat
                           , Map<String,String> props) throws IOException
    {
        FileOutputStream out = new FileOutputStream(dest);
        try {
            RDFWriter w = model.getWriter(sFormat);
            for ( String k : props.keySet() ) { w.setProperty(k, props.get(k)); }
            //w.setProperty("allowBadURIs", "true");
            w.write(model, out, null);
            out.flush();
        }
        finally {
            out.close();
        }
    }

    /*
    public static void storeAsEDM(Model m, File out) throws IOException
    {
        new EDMXMLWriter(EDMXMLWriter.ALL_CLASSES).write(m, out);
    }
    */

    public static class ResourceComparator implements Comparator<Resource>
    {
        @Override
        public int compare(Resource r1, Resource r2)
        {
            return r1.getURI().compareTo(r2.getURI());
        }
    }
}
