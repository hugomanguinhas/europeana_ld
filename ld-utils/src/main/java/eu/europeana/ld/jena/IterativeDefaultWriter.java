/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class IterativeDefaultWriter implements IterativeRecordWriter
{
    private Lang         _format;
    private OutputStream _out;

    public IterativeDefaultWriter(Lang format) { _format = format; }

    @Override
    public void init(OutputStream out) throws IOException { _out = out; }

    @Override
    public void write(Resource r) throws IOException
    {
        r.getModel().write(_out, _format.getLabel());
    }

    @Override
    public void close() throws IOException
    {
        _out.close();
    }
}
