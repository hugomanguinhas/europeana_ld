/**
 * 
 */
package eu.europeana.ld.harvester;

import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.ResourceCallback;


/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Nov 2016
 */
public interface LDHarvesterCallback extends ResourceCallback<Resource>
{
    public void begin();

    public void finish();
}
