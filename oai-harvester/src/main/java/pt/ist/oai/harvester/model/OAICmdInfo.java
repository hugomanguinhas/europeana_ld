package pt.ist.oai.harvester.model;

import java.util.Date;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public interface OAICmdInfo
{

    public long    getCursor();
    public boolean hasCompleteListSize();
    public long    getCompleteListSize();
    public boolean hasExpirationDate();
    public Date    getExpirationDate();
}
