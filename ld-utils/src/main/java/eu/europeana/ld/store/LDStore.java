/**
 * 
 */
package eu.europeana.ld.store;

import java.io.InputStream;

import org.apache.jena.rdf.model.Model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 May 2016
 */
public interface LDStore
{
    public void begin();

    public boolean contains(String uri);

    public void store(String uri, Model content);

    public void store(String uri, String content);

    public void store(String uri, InputStream content);

    public void commit();
}
