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
public class LDParalellHarvester_2
{
    private static final int DEFAULT_THREAD_COUNT = 20;

    private Dereferencer _deref;
    private int          _threadCount;

    public LDParalellHarvester_2(Dereferencer deref)
    {
        this(deref, DEFAULT_THREAD_COUNT);
    }

    public LDParalellHarvester_2(Dereferencer deref, int threadCount)
    {
        _deref       = deref;
        _threadCount = threadCount;
    }

    public void harvest(Collection<String> uris, LDStore store)
    {
        HarvesterWorker worker  = new HarvesterWorker(uris, store);
        System.out.println("new pool");
        ThreadFactory f;
        ExecutorService service = Executors.newFixedThreadPool(_threadCount);
        System.out.println("begin");
        store.begin();
        System.out.println("execute");
        try     {
            service.submit(worker);

            System.out.println("shutdown");
            service.shutdown();

            System.out.println("await");
            try { service.awaitTermination(1, TimeUnit.DAYS); }
            catch (InterruptedException e) {}
        }
        finally { store.commit(); }
    }

    private boolean harvest(String uri, LDStore store)
    {
        if ( store.contains(uri) ) { return false; }

        Model model = null;
        try                   { model = _deref.dereference(uri); }
        catch (IOException e) { logException(e); return false;   }

        if ( model == null || model.isEmpty() ) { logEmpty(uri); return false; }

        store.store(uri, model);
        return true;
    }

    private void logEmpty(String uri) {}

    private void logException(IOException e) {}

    private void log(String uri, int cursor, int length)
    {
        System.out.println("Harvested [" + cursor + " of " + length + "]: "
                       + uri + " ");
    }

    public class HarvesterWorker implements Runnable
    {
        private Queue<String> _queue  = new ConcurrentLinkedQueue<String>();
        private LDStore       _store  = null;
        private int           _cursor = 0;
        private int           _length = 0;

        public HarvesterWorker(Collection<String> uris, LDStore store)
        {
            _queue.addAll(uris);
            _store  = store;
            _length = _queue.size();
        }

        @Override
        public void run()
        {
            int cursor = 0;
            while ( true )
            {
                String uri = _queue.poll();
                if ( uri == null ) { return; }

                cursor = _cursor++;
                boolean ret    = harvest(uri, _store);
                if ( ret ) { log(uri, cursor, _length); }
            }
        }
    }
}