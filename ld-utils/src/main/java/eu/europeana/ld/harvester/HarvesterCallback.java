/**
 * 
 */
package eu.europeana.ld.harvester;

import org.apache.jena.rdf.model.Resource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public interface HarvesterCallback
{
    public void handle(Resource r, Status s);

    public static class Status
    {
        public long cursor;
        public long total;

        public Status(long total, long cursor)
        {
            this.total = total; this.cursor = cursor;
        }
    }
}
