/**
 * 
 */
package eu.europeana.ld.harvester;

import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.store.LDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 21 Jun 2016
 */
public class StoreCallback implements HarvesterCallback
{
    private LDStore _store;

    public StoreCallback(LDStore store) { _store = store; }

    @Override
    public void handle(Resource r, Status status)
    {
        String uri = r.getURI();
        if ( _store.contains(uri) )
        {
            System.out.println("exists in store!"); return;
        }
        _store.store(uri, r.getModel());
    }

    public void begin()  { _store.begin();  }

    public void finish() { _store.commit(); }
}
