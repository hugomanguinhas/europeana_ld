/**
 * 
 */
package pt.ist.oai.harvester.verb;

import org.apache.commons.io.IOUtils;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIRecord;
import pt.ist.util.iterator.CloseableIterable;
import pt.ist.util.iterator.CloseableIterator;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class RunIterateRecords
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://www.mimo-db.eu:8080/oaicat/OAIHandler";
        //String baseURL = "http://oai.europeana.eu/oaicat/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        CloseableIterable<OAIRecord> iter = harvester.iterateRecords("MU", "lido");
        try {
            for ( OAIRecord record : iter )
            {
                System.out.println(record.getHeader().getIdentifier());
            }
        }
        finally { iter.close(); }

//        String baseURL = "http://oai.europeana.eu/oaicat/OAIHandler";
//        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
//        long ret = harvester.countRecords("2059211_Ag_EU_eSOUNDS_1019_CNRS_MMSH", "edm");
//        System.err.println("ret[" + ret + "]");
    }
}
