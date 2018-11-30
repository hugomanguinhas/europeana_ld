/**
 * 
 */
package eu.europeana.ld.tools;

import java.io.PrintStream;
import java.text.DecimalFormat;

import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.harvester.LDHarvesterCallback;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 19 Oct 2016
 */
public class ProgressHarvesterCallback implements LDHarvesterCallback
{
    private DecimalFormat    _df   = new DecimalFormat("00.0%");
    private String           _prev = null;
    private PrintStream      _ps   = null;
    private ResourceCallback _cb   = null;

    public ProgressHarvesterCallback(ResourceCallback cb, PrintStream ps)
    {
        _ps  = ps;
        _cb = cb;
    }

    @Override
    public void handle(String id, Resource r, Status status)
    {
        _cb.handle(id, r, status);
        printStatus(status);
    }

    @Override
    public void begin()
    {
        if ( !(_cb instanceof LDHarvesterCallback) ) { return; }
        ((LDHarvesterCallback)_cb).begin();
        _prev = _df.format(0);
        _ps.print(_prev);
    }

    @Override
    public void finish()
    {
        if ( !(_cb instanceof LDHarvesterCallback) ) { return; }
        ((LDHarvesterCallback)_cb).finish();
    }

    protected void printStatus(Status status)
    {
        double percent = (double)status.cursor / status.total;
        String str = _df.format(percent);
        if ( str.equals(_prev) ) { return; }

        _ps.print("\b\b\b\b\b");
        _prev = str;
        _ps.print(str);
    }
}
