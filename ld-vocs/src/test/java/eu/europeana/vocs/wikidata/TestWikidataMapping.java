/**
 * 
 */
package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Jun 2016
 */
public class TestWikidataMapping
{
    public static final void main(String[] args) throws Throwable
    {
        String      path = "etc/mappings/wikidata2agent.xsl";
        InputStream mis  = ClassLoader.getSystemResourceAsStream(path);
        if ( mis == null ) { return; }

        Transformer t = TransformerFactory.newInstance()
                                          .newTransformer(new StreamSource(mis));

        String      data = "etc/data/wikidata/humans_Q5/humans_Q5.xml";
        InputStream dis  = ClassLoader.getSystemResourceAsStream(data);
        if ( dis == null ) { return; }

        File        out  = new File("D:\\work\\git\\Europeana\\ld\\ld-vocs\\src"
                                  + "\\test\\resources\\etc\\data\\wikidata"
                                  + "\\humans_Q5\\humans_edm.xml");

        t.transform(new StreamSource(dis), new StreamResult(out));
    }
}
