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
public class TestEdmExt2Int
{

    public static final void main(String[] args) throws Exception
    {
        File src  = new File("D:\\work\\incoming\\edmExt2Int\\data\\bnf.xml");
        File dst  = new File("D:\\work\\incoming\\edmExt2Int\\data\\bnf_internal.xml");

        File file = new File("D:\\work\\incoming\\edmExt2Int\\example_1.xsl");
        TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
        Transformer        t  = tf.newTransformer(new StreamSource(file));
        t.setParameter("datasetId", "9200357");
        t.transform(new StreamSource(src), new StreamResult(dst));
    }
}
