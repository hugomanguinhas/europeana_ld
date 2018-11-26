/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 28 Mar 2018
 */
public class TestEDMXSDSort
{

    public static final void main(String[] args) throws Exception
    {
        File src  = new File("D:\\work\\incoming\\newspapers\\metadata\\example\\bnf_fixed.xml");
        File dst  = new File("D:\\work\\incoming\\newspapers\\metadata\\example\\bnf_fixed_ordered.xml");

        File file = new File("D:\\work\\incoming\\newspapers\\metadata\\script\\EDM.XSD.sorter.xsl");
        TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
        Transformer        t  = tf.newTransformer(new StreamSource(file));
        t.transform(new StreamSource(src), new StreamResult(dst));
    }
}
