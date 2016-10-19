/**
 * 
 */
package eu.europeana.ld.deref;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 May 2016
 */
public interface Dereferencer
{
    public Model dereference(String uri) throws IOException;

    public Model dereference(String url, String mime) throws IOException;
}
