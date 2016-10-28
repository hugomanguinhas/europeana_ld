/**
 * 
 */
package eu.europeana.ld.harvester;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.deref.Dereferencer;
import eu.europeana.ld.store.LDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 May 2016
 */
public class LDParalellHarvester
{
    private static final int DEFAULT_THREAD_COUNT = 20;

    private Dereferencer _deref;
    private int          _threadCount;
    private LDStore      _store = null;
    private int          _size  = 0;

    public LDParalellHarvester(Dereferencer deref)
    {
        this(deref, DEFAULT_THREAD_COUNT);
    }

    public LDParalellHarvester(Dereferencer deref, int threadCount)
    {
        _deref       = deref;
        _threadCount = threadCount;
    }

    public void harvest(Collection<String> uris, LDStore store)
    {
        _store = store;
        _size  = uris.size();

        ExecutorService service = Executors.newFixedThreadPool(_threadCount);
        store.begin();
        try {
            int i = 1;
            for ( String uri : uris ) { service.submit(new HarvesterTask(uri
                                                                       , i++)); }

            service.shutdown();

            try { service.awaitTermination(1, TimeUnit.DAYS); }
            catch (InterruptedException e) {}
        }
        finally { store.commit(); }
    }

    private void logEmpty(String uri) {}

    private void logException(String uri, Throwable t, int cursor, int length)
    {
        System.err.println("Harvested [" + cursor + " of " + length + "]: "
                         + uri + " [error found]");
        t.printStackTrace();
    }

    private void log(String uri, Model model, int cursor, int length)
    {
        System.out.println("Harvested [" + cursor + " of " + length + "]: "
                         + uri + " [" + model.size() + "]");
    }

    public class HarvesterTask implements Runnable
    {
        private String  _uri   = null;
        private int     _index = 0;

        public HarvesterTask(String uri, int index)
        {
            _uri   = uri;
            _index = index;
        }

        @Override
        public void run()
        {
            try {
                if ( _store.contains(_uri) ) { return; }

                Model model = _deref.dereference(_uri);
                if ( model == null || model.isEmpty() ) { logEmpty(_uri); return; }

                log(_uri, model, _index, _size);

                _store.store(_uri, model);
            }
            catch (Throwable t) { logException(_uri, t, _index, _size); }
        }
    }
}