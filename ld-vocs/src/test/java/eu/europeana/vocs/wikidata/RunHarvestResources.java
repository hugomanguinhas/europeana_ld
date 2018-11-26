/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.harvester.LDParalellHarvester;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.sparql.SPARQLDereferencer;
import eu.europeana.ld.store.ZipLDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class RunHarvestResources
{
    private static String _endpoint = "https://query.wikidata.org/sparql";

    private static LDParalellHarvester _harvester
        = new LDParalellHarvester(new SPARQLDereferencer(_endpoint));

    private static File _src 
        = new File("D:\\work\\data\\hackweek\\dataset\\agents\\agents_wkd.csv");

//    private static File _src 
//        = new File("D:\\work\\data\\entities\\agents\\wikidata\\");

    //change file:///D:/work/eclipse/hackweek/RDF/
    //to     http://purl.org/collections/nl/rma/
    
    
    public static final void main(String[] args) throws IOException
    {
        if ( !_src.isDirectory() ) { processDataset(_src); return; }

        for ( File file : _src.listFiles() )
        {
            if ( !file.getName().endsWith(".csv") ) { continue; }
            processDataset(file);
        }
    }

    public static void processDataset(File file)
    {
        try {
            System.out.println("Processing file: " + file.getAbsolutePath());
            Collection<String> uris = loadURIs(file);

            File dstZip = getFile(file, ".zip");
            System.out.println();
            System.out.println("Harvesting to file: " + dstZip.getAbsolutePath());
            _harvester.harvest(uris, new ZipLDStore(dstZip, Lang.TTL));

            File dstXML = getFile(file, ".xml");
            System.out.println();
            System.out.println("Flattening to file: " + dstXML.getAbsolutePath());
            Model model = flatten(dstZip, dstXML);

            File dstRPT = getFile(file, ".rpt.txt");
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
