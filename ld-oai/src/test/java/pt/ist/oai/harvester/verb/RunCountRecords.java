/**
 * 
 */
package pt.ist.oai.harvester.verb;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class RunCountRecords
{

    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://www.mimo-db.eu:8080/oaicat/OAIHandler";
        //String baseURL = "http://oai.europeana.eu/oaicat/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        long ret = harvester.countRecords("MU", "lido");
        System.out.println("ret[" + ret + "]");

//        String baseURL = "http://oai.europeana.eu/oaicat/OAIHandler";
//        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
//        long ret = harvester.countRecords("2059211_Ag_EU_eSOUNDS_1019_CNRS_MMSH", "edm");
//        System.err.println("ret[" + ret + "]");
    }
}
