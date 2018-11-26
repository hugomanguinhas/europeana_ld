/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.deref.Dereferencer;
import eu.europeana.ld.harvester.LDParalellHarvester;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.store.LDStore;
import eu.europeana.ld.store.ZipLDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class ResourceListHarvester
{
    private Dereferencer _deref;

    public ResourceListHarvester(Dereferencer deref)
    {
        _deref = deref;
    }

    public void harvest(File src)
    {
        harvest(src, getFile(src, ".zip"));
    }

    /*
    public void harvest(File src, File dst)
    {
        harvest(src, );
    }
    */

    public void harvest(File src, File dst)
    {
        LDStore store = new ZipLDStore(dst, Lang.TTL);
        LDParalellHarvester harvester = new LDParalellHarvester(_deref);
        try {
            System.out.println("Processing file: " + src.getAbsolutePath());
            Collection<String> uris = loadURIs(src);

            System.out.println();
            System.out.println("Harvesting to file: " + dst.getAbsolutePath());
            harvester.harvest(uris, store);

            File dstXML = getFile(src, ".xml");
            System.out.println();
            System.out.println("Flattening to file: " + dstXML.getAbsolutePath());
            Model model = flatten(dst, dstXML);

            File dstRPT = getFile(src, ".rpt.txt");
            System.out.println();
            System.out.println("Building report to file: " + dstRPT.getAbsolutePath());
            new WikidataAnalysis(uris).analyse(model).print(dstRPT);
        }
        catch (Throwable t) { t.printStackTrace(); }
    }

    private static File getFile(File file, String suffix)
    {
        String name = file.getName().replace(".csv", suffix);
        return new File(file.getParentFile(), name);
    }

    public static Model flatten(File src, File dst) throws IOException
    {
        //if ( dst.exists() ) { return ; }
        Model model = JenaUtils.loadAll(src);
        JenaUtils.store(model, dst);
        return model;
    }

    public static Collection<String> loadURIs(File src)
    {
        Collection<String> s = new TreeSet<String>();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(src));
    
            String sLine;
            while ( (sLine = r.readLine()) != null )
            {
                sLine = sLine.trim();
                if ( sLine.isEmpty() ) { continue; }

                s.add(sLine);
            }
        }
        catch (IOException e) {}
        finally { IOUtils.closeQuietly(r); }

        return s;
    }
}
