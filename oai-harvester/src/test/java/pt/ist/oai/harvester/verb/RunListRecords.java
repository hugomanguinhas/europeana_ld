/**
 * 
 */
package pt.ist.oai.harvester.verb;

import java.util.List;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIRecord;
import pt.ist.oai.harvester.model.OAIRequest;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunListRecords
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        OAIRequest<List<OAIRecord>> ret = harvester.newListRecords("gallica:theme:1:19", "oai_dc");
        List<OAIRecord> list = ret.handle();
        System.err.println("ret[" + ret.getInfo().getCompleteListSize() + "," + list.size() + "]");
    }
}
