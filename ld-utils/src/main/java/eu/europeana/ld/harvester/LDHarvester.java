/**
 * 
 */
package eu.europeana.ld.harvester;

import java.io.Closeable;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.ResourceCallback;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 1 Jul 2016
 */
public interface LDHarvester extends Closeable
{
    public Resource harvest(String uri);

    public Model    harvest(Collection<String> uris);

    public Model    harvestAll();

    public void harvest(String uri, ResourceCallback callback);

    public void harvest(Collection<String> uris, ResourceCallback callback);

    public void harvestAll(ResourceCallback callback);
}
