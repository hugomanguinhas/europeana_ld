package eu.europeana.ld.jena;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.shared.JenaException;
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

    public static String getQName(Resource r)
    {
        if ( r == null  ) { return null; }

        String ns = r.getNameSpace();
        if ( ns == null ) { return r.getURI(); }

        String p  = r.getModel().getNsURIPrefix(ns);
        return ( p == null ? r.getURI() : (p + ":" + r.getLocalName()) );
    }

    public static Model load(File file)
    {
        return load(file, ModelFactory.createDefaultModel());
    }

    public static Model load(File file, Model model)
    {
        Lang lang = RDFLanguages.filenameToLang(file.getName());
        if ( lang == null ) { return null; }

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            RDFDataMgr.read(model, is, lang);
            return model;
        } catch (IOException e) {
            System.out.println("Could not read file: " + file.getName()
                             + ", reason:" + e.getMessage());
        }
        finally { closeQuietly(is); }
        return model;
    }


    /***************************************************************************
     * Loading of Multiple Resources
     **************************************************************************/

    public static boolean isCompressed(File file)
    {
        List l = Arrays.asList("zip", "tgz");
        String ext = FileUtils.getFilenameExt(file.getName());
        return l.contains(ext);
    }

    public static Model loadAll(File file)
    {
        return loadAll(file, ModelFactory.createDefaultModel());
    }

    public static Model loadAll(File file, Model m)
    {
        if ( file == null || !file.exists() ) { return m; }

        if ( file.isDirectory() ) { return loadAllFromDIR(file, m); }

        if ( isCompressed(file) ) { return loadAllFromZIP(file, m); }

        return m;
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


    /*
    public static Model loadModel(File file)
    {
        Lang lang = RDFLanguages.filenameToLang(file.getName());
        if ( lang == null ) { return null; }

    }
    */

    private static Model loadAllFromZIP(File file, Model m)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);

        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(file));

            ZipEntry entry = zis.getNextEntry();
            while ( entry != null )
            {
                Lang lang = RDFLanguages.filenameToLang(entry.getName());
                if ( lang == null ) { entry = zis.getNextEntry(); continue; }

                IOUtils.copy(zis, baos);

                //String label = lang.getLabel();
                //RDFDataMgr.read(m, zis, lang);
                String label = lang.getLabel();
                RDFReader reader = m.getReader(label);
                reader.setProperty("allowBadURIs", "true");
                try {
                    reader.read(m, new ByteArrayInputStream(baos.toByteArray()), label);
                }
                catch(JenaException e) {
                    System.err.println("Error processing file: " + entry.getName());
                    e.printStackTrace();
                }
                //RDFDataMgr.read(m, new ByteArrayInputStream(baos.toByteArray()), lang);
                //m.read(zis, lang.getLabel());

                baos.reset();

                entry = zis.getNextEntry();
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException           e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(zis); }

        return m;
    }

    private static Model loadAllFromDIR(File dir, Model m)
    {
        for ( File file : dir.listFiles() )
        {
            if ( file.isDirectory() ) { loadAllFromDIR(file, m); continue; }

            load(file, m);
        }
        return m;
    }


    /***************************************************************************
     * Storing
     **************************************************************************/

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
        finally { IOUtils.closeQuietly(out); }
    }

    /*
    public static void storeAsEDM(Model m, File out) throws IOException
    {
        new EDMXMLWriter(EDMXMLWriter.ALL_CLASSES).write(m, out);
    }
    */

    public static Model importNamespaces(Model src, Model dst)
    {
        NsIterator iter = src.listNameSpaces();
        try {
            while ( iter.hasNext() )
            {
                String ns = iter.nextNs();
                dst.setNsPrefix(src.getNsURIPrefix(ns), ns);
            }
        }
        finally { iter.close(); }

        return dst;
    }

    public static class ResourceComparator implements Comparator<Resource>
    {
        @Override
        public int compare(Resource r1, Resource r2)
        {
            return r1.getURI().compareTo(r2.getURI());
        }
    }
}
