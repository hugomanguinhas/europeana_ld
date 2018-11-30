/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Resource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Nov 2016
 */
public interface IterativeRecordWriter extends Closeable
{
    public void init(OutputStream out) throws IOException;

    public void write(Resource r) throws IOException;
}
