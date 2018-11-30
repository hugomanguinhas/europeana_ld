/**
 * 
 */
package eu.europeana.ld.edm.io;

import org.apache.jena.riot.Lang;

import eu.europeana.ld.edm.EuropeanaDataUtils;
import eu.europeana.ld.io.FileNaming;
import eu.europeana.ld.io.FileNaming.DefaultFileNaming;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class EDMLocalIdFileNaming extends FileNaming.DefaultFileNaming 
                                  implements FileNaming
{
    @Override
    public String convert(String uri, Lang format)
    {
        String[] id = EuropeanaDataUtils.getDatasetAndLocalID(uri);
        if ( id == null ) { return null; }

        return appendExtension(id[1], format);
    }
}
