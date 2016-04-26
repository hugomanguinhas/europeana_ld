/**
 * 
 */
package pt.ist.oai.harvester.verb;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAIRecord;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class RunGetRecord
{
    public static final void main(String[] args) throws Exception
    {
        String baseURL = "http://www.mimo-db.eu:8080/oaicat/OAIHandler";
        OAIHarvester harvester = new OAIHarvesterImpl(baseURL);
        OAIRecord ret = harvester.getRecord("oai:oaicat.mimo:OAI_ULEI_M0000086", "lido");
        System.err.println("ret=" + ret);
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(ret.getMetadata()), new StreamResult(System.err));
    }
}