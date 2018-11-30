/**
 * 
 */
package eu.europeana.ld.mongo;

import org.bson.conversions.Bson;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.harvester.LDHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 May 2017
 */
public interface MongoHarvester extends LDHarvester
{
    public void harvestBySearch(String query, ResourceCallback cb);

    public void harvestBySearch(Bson filter, ResourceCallback cb);

}
