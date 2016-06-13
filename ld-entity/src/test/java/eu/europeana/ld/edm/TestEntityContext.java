package eu.europeana.ld.edm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.out.*;

public class TestEntityContext {

    public static void main(String[] args) throws IOException
    {
        //File file = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\EDM\\jsonld\\agent.xml");
        File file = new File("D:\\work\\incoming\\F&D\\enrichment API\\context_test_record.jsonld");

        Model m = ModelFactory.createDefaultModel();
        
        try {
            m.read(new FileInputStream(file), null, "JSONLD");
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            return;
        }

//        URL url = new URL("file:///C:/Users/Hugo/Google%20Drive/Europeana/EDM/jsonld/context.jsonld");
        URL url = new URL("file:///D:/work/incoming/F&D/enrichment%20API/context.jsonld");
        new JsonLdWriter(url).write(m, new OutputStreamWriter(System.out));
    }
}
