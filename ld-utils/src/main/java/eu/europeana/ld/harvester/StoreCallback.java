/**
 * 
 */
package eu.europeana.ld.harvester;

import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.Logger;

import eu.europeana.ld.store.LDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 21 Jun 2016
 */
public class StoreCallback implements LDHarvesterCallback
{
    private static Logger _log = Logger.getLogger(LDHarvester.class);

    private LDStore _store;

    public StoreCallback(LDStore store) { _store = store; }

    @Override
    public void handle(String uri, Resource r, Status status)
    {
        if ( _store.contains(uri) ) { _log.info("exists in store!"); return; }
        _store.store(uri, r.getModel());
    }

    @Override
    public void begin()  { _store.begin();  }

    @Override
    public void finish() { _store.commit(); }
}
