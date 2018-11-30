/**
 * 
 */
package eu.europeana.ld.edm.io;

import org.apache.jena.riot.Lang;

import eu.europeana.ld.edm.EuropeanaDataUtils;
import eu.europeana.ld.io.FileNaming;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class EDMDatasetFileNaming extends FileNaming.DefaultFileNaming 
                                  implements FileNaming
{
    @Override
    public String convert(String id, Lang format)
    {
        return appendExtension(getName(id), format);
    }

    private String getName(String id)
    {
        if ( id.startsWith("/") ) { return id.substring(1); }

        String ns = EuropeanaDataUtils.CHO_NS;
        if ( id.startsWith(ns) ) { return id.substring(ns.length()); }

        return id;
    }
}
