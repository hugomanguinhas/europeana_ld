/**
 * 
 */
package pt.ist.oai.harvester.verb;

import java.util.Random;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIRecordHeader;
import pt.ist.util.iterator.CloseableIterable;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunIterateIdentifiers
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        int i = 0;
        CloseableIterable<OAIRecordHeader> iter
            = harvester.iterateIdentifiers("gallica:theme:1:19", "oai_dc");
        try {
            for(OAIRecordHeader header : iter)
            {
                if(new Random().nextInt(1000) < 1) { break; }
                System.err.println("header[" + (++i) + "]=" + header.getIdentifier());
            }
        }
        finally { iter.close(); }
    }
}
