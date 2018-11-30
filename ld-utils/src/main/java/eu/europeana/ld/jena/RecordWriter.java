/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Resource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public interface RecordWriter
{
    public void write(Resource r, OutputStream out) throws IOException;
}
