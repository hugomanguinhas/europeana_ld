/**
 * 
 */
package pt.ist.oai.harvester.verb;

import java.util.List;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIRecordHeader;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunListIdentifiers
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://oai.bnf.fr/oai2/OAIHandler";
        //String baseURL = "http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        List<OAIRecordHeader> ret = harvester.listIdentifiers("gallica:theme:8:80", "oai_dc");
        System.err.println("ret[" + ret.size() + "]");
    }
}
