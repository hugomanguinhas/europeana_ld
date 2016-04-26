/**
 * 
 */
package pt.ist.oai.harvester.verb;

import java.util.List;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIMetadataFormat;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunListMetadataFormats
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://bd2.inesc-id.pt:8080/repox2Eudml/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        List<OAIMetadataFormat> ret = harvester.listMetadataFormats();
        System.err.println("ret=" + ret);
    }
}
