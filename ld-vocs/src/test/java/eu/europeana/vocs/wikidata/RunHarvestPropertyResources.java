/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.harvester.LDParalellHarvester;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.sparql.SPARQLDereferencer;
import eu.europeana.ld.store.ZipLDStore;
import static eu.europeana.vocs.wikidata.Wikidata.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class RunHarvestPropertyResources
{
    private static Property[] _properties
        = { /*P39, */P101, P106 };

    private static String _prefix = "agents_";
    private static File   _src    = new File("D:\\work\\data\\entities\\agents\\wikidata\\agents_wkd.ttl");


    public static final void main(String[] args) throws IOException
    {
        Model model = JenaUtils.load(_src);

        String endpoint = "https://query.wikidata.org/sparql";
        LDParalellHarvester harvester
            = new LDParalellHarvester(new SPARQLDereferencer(endpoint));

        Collection<String> uris = new TreeSet();

        for ( Property p : _properties )
        {
            uris.clear();

            StmtIterator iter = model.listStatements(null, p, (RDFNode)null);
            try {
                while ( iter.hasNext() )
                {
                    RDFNode o = iter.next().getObject();
                    if ( o.isURIResource() ) { uris.add(o.asResource().getURI()); }
                }
            }
            finally { iter.close(); }

            printCSV(uris, getFile(p, ".csv"));

            File dst2 = getFile(p, ".zip");
            harvester.harvest(uris, new ZipLDStore(dst2, Lang.TTL));

            flatten(dst2, getFile(p, ".xml"));
        }

        model.close();
    }

    private static File getFile(Property p, String suffix)
    {
        String uri      = p.getURI();
        String propname = uri.substring(uri.lastIndexOf('/')+1);
        String filename = _prefix + propname + suffix;
        return new File(_src.getParentFile(), filename);
    }

    private static void printCSV(Collection<String> uris, File dst)
            throws IOException
    {
        if ( dst.exists() ) { return; }

        PrintStream ps = new PrintStream(dst);
        try {
            for ( String uri : uris ) { ps.println(uri); }
        }
        finally { ps.close(); }
    }

    public static void flatten(File src, File dst) throws IOException
    {
        if ( dst.exists() ) { return; }
        Model model = JenaUtils.loadAll(src);
        JenaUtils.store(model, dst);
        model.close();
    }
}
