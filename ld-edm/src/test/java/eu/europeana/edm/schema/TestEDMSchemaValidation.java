package eu.europeana.edm.schema;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class TestEDMSchemaValidation {

    public void validate(File source, URL schemaFile) throws SAXException, IOException
    {
        Source xmlFile = new StreamSource(source);
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        try {
          validator.validate(xmlFile);
          System.out.println(xmlFile.getSystemId() + " is valid");
        } catch (SAXException e) {
          System.out.println(xmlFile.getSystemId() + " is NOT valid");
          System.out.println("Reason: " + e.getLocalizedMessage());
        }
    }

    public static void main(String... args) throws Exception
    {
        URL schemaFile = new URL("file:///D:/work/incoming/edm.xsd/corelib/EDM.xsd");
        File src = new File("D:/work/incoming/metis/test_record_new.xml");

        File dst  = new File("D:/work/incoming/metis/test_record_new_ordered.xml");

        File file = new File("D:\\work\\incoming\\edmExt2Int\\EDM.XSD.sorter.xsl");
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer        t  = tf.newTransformer(new StreamSource(file));
        t.transform(new StreamSource(src), new StreamResult(dst));

        new TestEDMSchemaValidation().validate(dst, schemaFile);
    }
}
