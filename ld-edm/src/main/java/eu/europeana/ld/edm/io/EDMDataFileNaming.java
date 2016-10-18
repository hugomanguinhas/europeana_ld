/**
 * 
 */
package eu.europeana.ld.edm.io;

import org.apache.jena.riot.Lang;

import eu.europeana.ld.edm.EuropeanaDataUtils;
import eu.europeana.ld.store.ZipLDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class EDMDataFileNaming extends    ZipLDStore.DefaultFileNaming 
                               implements ZipLDStore.FileNaming
{
    @Override
    public String convert(String uri, Lang format)
    {
        String ns = EuropeanaDataUtils.CHO_NS;
        if ( !uri.startsWith(ns) ) { return null; }
        return appendExtension(uri.substring(ns.length()), format);
    }
}
