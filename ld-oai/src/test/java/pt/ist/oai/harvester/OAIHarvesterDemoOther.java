package pt.ist.oai.harvester;

import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIHarvesterDemoOther
{
    public static void main(String[] args)
    {
        OAIHarvester harvester = new OAIHarvesterImpl("http://bd2.inesc-id.pt:8080/repox2/OAIHandler");
        //OAIHarvester harvester = new OAIHarvesterImpl("http://oai.d.efg.research-infrastructures.eu/oai.do");

        System.err.println(harvester.listMetadataFormats());

        //Iterate one by one
        CloseableIterable<OAIRecord> result = harvester.iterateRecords("08603_Ag_EU_EFG_FilmArchive", "ese");
        //CloseableIterable<OAIRecord> result = harvester.iterateRecords("08714f15-e7f4-43ca-b49c-5cb4d3eb1e64", "ese");
        try {
            for(OAIRecord record : result)
            {
                 System.err.println(record.getHeader().getIdentifier());
                 break;
            }
        }
        finally { result.close(); }
    }
}
