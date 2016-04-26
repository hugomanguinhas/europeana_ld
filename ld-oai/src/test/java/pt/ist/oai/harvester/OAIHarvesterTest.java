package pt.ist.oai.harvester;

import java.io.IOException;
import java.util.Properties;

import pt.ist.oai.harvester.model.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIHarvesterTest
{
    public static void main(String[] args) throws IOException
    {
        Properties props = new Properties();
        props.put("set", "CEDRAM");
        props.put("metadataPrefix", "eudml-article");

        OAIHarvester harvester = new OAIHarvesterImpl("http://bd2.inesc-id.pt:8080/repox2Eudml/OAIHandler");        
        OAIRecord record = harvester.getRecord("urn:eudml.eu:GDZ_Band:oai:eudml.mathdoc.fr:GDZ_Band:PPN589309803", "eudml-book");
        System.err.println("record=" + record);
    }
}
