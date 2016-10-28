/**
 * 
 */
package eu.europeana.ld.harvester;

import java.io.IOException;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.deref.Dereferencer;
import eu.europeana.ld.store.LDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 May 2016
 */
public class LDDerefHarvester
{
    private Dereferencer _deref;

    public LDDerefHarvester(Dereferencer deref) { _deref = deref; }

    public void harvest(Collection<String> uris, LDStore store)
    {
        store.begin();
        try {
            int i = 1;
            int l = uris.size();
            for (String uri : uris) { log(uri, i++, l); harvest(uri, store); }
        }
        finally { store.commit(); }
    }

    private void log(String uri, int cursor, int length)
    {
        System.out.print("Harvesting [" + cursor + " of " + length + "]: "
                       + uri + " ");
    }

    private void harvest(String uri, LDStore store)
    {
        Model model = null;
        try { model = _deref.dereference(uri); } catch (IOException e) {}

        if ( model == null || model.isEmpty() )
        {
            System.out.println("Empty!"); return;
        }

        System.out.println("size [" + model.size() + "], stored!");
        store.store(uri, model);
    }
}