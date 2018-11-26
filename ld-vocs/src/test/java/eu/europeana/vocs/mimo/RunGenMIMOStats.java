/**
 * 
 */
package eu.europeana.vocs.mimo;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.skos.SKOSAnalysis;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Jul 2016
 */
public class RunGenMIMOStats
{
    

    public static final void main(String[] args) throws IOException
    {
        File rpt  = new File("D:/work/git/Europeana/ld/ld-vocs/src/main/resources/etc/vocs/mimo.stat.txt");
        File rpt2 = new File("D:/work/git/Europeana/ld/ld-vocs/src/main/resources/etc/vocs/mimo_hs.stat.txt");

        Model model = ModelFactory.createDefaultModel();
        String[] rsrcs = { "etc/vocs/mimo_hs.xml"
                         , "etc/vocs/mimo_keywords.xml" };
        for ( String r : rsrcs )
        {
            JenaUtils.load(new File(ClassLoader.getSystemResource(r).getFile())
                         , model);
        }

        new SKOSAnalysis().analyse(model).print(rpt);

        model.removeAll();

        JenaUtils.load(new File(ClassLoader.getSystemResource(rsrcs[0]).getFile())
                     , model);

        new SKOSAnalysis().analyse(model).print(rpt2);
    }
}
