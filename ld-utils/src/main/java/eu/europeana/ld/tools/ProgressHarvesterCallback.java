/**
 * 
 */
package eu.europeana.ld.tools;

import java.io.PrintStream;
import java.text.DecimalFormat;

import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.harvester.HarvesterCallback;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 19 Oct 2016
 */
public class ProgressHarvesterCallback implements HarvesterCallback
{
    private DecimalFormat _df   = new DecimalFormat("00.0%");
    private String        _prev = null;
    private PrintStream   _ps   = null;

    public ProgressHarvesterCallback(PrintStream ps) { _ps  = ps; }

    @Override
    public void handle(Resource r, Status status) { printStatus(status); }

    protected void printStatus(Status status)
    {
        double percent = (double)status.cursor / status.total;
        String str = _df.format(percent);
        if ( str.equals(_prev) ) { return; }

        if ( _prev != null ) { _ps.print("\b\b\b\b\b"); }
        _prev = str;
        _ps.print(str);
    }
}
