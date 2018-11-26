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

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class RunFlattenResources
{
    private static File _src = new File("D:\\work\\data\\entities\\agents\\wikidata\\agents_wkd.zip");

    public static final void main(String[] args) throws IOException
    {
        Model model = JenaUtils.loadAll(_src);

        File dst = new File(_src.getParentFile(), _src.getName().replace(".zip", ".ttl"));
        JenaUtils.store(model, dst);
    }
}
