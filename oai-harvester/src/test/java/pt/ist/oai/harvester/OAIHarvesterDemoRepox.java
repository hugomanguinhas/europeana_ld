package pt.ist.oai.harvester;

import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import pt.ist.oai.harvester.model.*;
import pt.ist.oai.harvester.model.rw.OAIWriter;
import pt.ist.util.iterator.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIHarvesterDemoRepox
{
    public static void main(String[] args) throws IOException
    {
        OAIHarvester harvester = new OAIHarvesterImpl("http://bd2.inesc-id.pt:8080/repox2/OAIHandler");
        System.err.println(harvester.identify());
        System.err.println(harvester.listSets());
        System.err.println(harvester.listMetadataFormats());

        //Iterate one by one
        OAIRequest<CloseableIterable<OAIRecord>> request
            = harvester.newIterateRecords("08603_Ag_EU_EFG_FilmArchive", "ese");
        new OAIWriter().write(request, null, new StreamResult(System.err));
    }
}
