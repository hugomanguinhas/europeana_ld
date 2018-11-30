/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.OutputStream;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class DefaultWriter implements RecordWriter
{
    private Lang _format;

    public DefaultWriter(Lang format) { _format = format; }

    @Override
    public void write(Resource r, OutputStream out)
    {
        r.getModel().write(out, _format.getLabel());
    }
}
