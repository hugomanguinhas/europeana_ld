/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.vocs.VocsUtils;
import eu.europeana.vocs.coref.CoReferenceFetcher;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class RunFetchWikidataCoref
{
    public static final void main(String[] args) throws IOException
    {
        File src = new File("D:\\work\\data\\entities\\agents.xml");
        File dst = new File("D:\\work\\data\\entities\\agents_coref_wkd.csv");
        Model model = JenaUtils.load(src);
        new CoReferenceFetcher(VocsUtils.PATTERN_WIKIDATA).fetch(model, dst);
    }
}
