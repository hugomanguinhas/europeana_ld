/**
 * 
 */
package eu.europeana.ld;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public interface ResourceCallback<R>
{
    public void handle(String id, R r, Status s);

    public static class Status
    {
        public long cursor;
        public long total;

        public Status() { this(0,0); }

        public Status(long total, long cursor)
        {
            this.total = total; this.cursor = cursor;
        }
    }
}
