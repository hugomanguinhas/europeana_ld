/**
 * 
 */
package eu.europeana.ld.skos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import eu.europeana.ld.jena.JenaUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 6 May 2016
 */
public class RunGenSKOS4ProviderNames
{

    public static final void main(String... args) throws IOException
    {
        String src  = "D:\\work\\git\\Europeana\\ld\\ld-utils\\src\\test\\resources\\etc\\api\\";
        String base = "http://data.europeana.eu/provider/";

        File dir = new File(src);
        for ( File file : dir.listFiles() )
        {
            String name = file.getName();
            if ( !name.endsWith(".api.xml") ) { continue; }

            FileInputStream fis = new FileInputStream(file);
            Model model = ModelFactory.createDefaultModel();
            try     { new APIFacets2SKOSExtractor(base).extract(fis, model); }
            finally { IOUtils.closeQuietly(fis);                             }

            File dst = new File(dir, name.replace(".api", ".skos"));
            JenaUtils.store(model, dst);
        }
    }
}
