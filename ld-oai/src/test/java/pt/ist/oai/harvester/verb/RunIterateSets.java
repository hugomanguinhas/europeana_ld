/**
 * 
 */
package pt.ist.oai.harvester.verb;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIMetadataSet;
import pt.ist.oai.harvester.model.OAIRequest;
import pt.ist.util.iterator.CloseableIterable;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunIterateSets
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        int i = 0;
        OAIRequest<CloseableIterable<OAIMetadataSet>> req = harvester.newIterateSets();
        CloseableIterable<OAIMetadataSet> iter = req.handle();
        try {
            for(OAIMetadataSet set : iter) {
                //if(new Random().nextInt(1000) < 1) break;
                System.err.println("set[" + (++i) + "][" + req.getInfo().getCompleteListSize() + "]=" + set);
            }
        }
        finally {
            iter.close();
        }
    }
}
