/**
 * 
 */
package eu.europeana.ld.harvester;

import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 1 Jul 2016
 */
public interface LDHarvester
{
    public Resource harvest(String uri);

    public Model    harvest(Collection<String> uris);

    public Model    harvestAll();

    public void harvest(String uri, HarvesterCallback callback);

    public void harvest(Collection<String> uris, HarvesterCallback callback);

    public void harvestAll(HarvesterCallback callback);

    public void close();
}
