/**
 * 
 */
package eu.europeana.edm.owl;

import java.io.File;
import java.io.IOException;

import eu.europeana.ld.edm.owl.OWLFormatsBuilder;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 5 Aug 2016
 */
public class GenOWLDefinitions
{
    private static OWLFormatsBuilder builder = new OWLFormatsBuilder();

    public static void main(String[] args) throws IOException
    {
        File file = new File("D:\\work\\git\\Europeana\\ld\\ld-edm\\src\\main\\resources\\etc\\owl");
        processFile(file);
    }

    private static void processFile(File dir) throws IOException
    {
        if ( !dir.isDirectory() ) { return; }
        
        for ( File file : dir.listFiles() )
        {
            if ( !file.getName().endsWith(".owl") ) { continue; }

            builder.generateFormats(file);
        }
    }

}