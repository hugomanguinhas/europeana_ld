/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.out.JsonLdCompactWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class JSONLDWriter implements RecordWriter
{
    private Charset CHARSET = Charset.forName("UTF-8");
    private JsonLdCompactWriter _writer;

    public JSONLDWriter(String url)
    {
        _writer = new JsonLdCompactWriter(url);
    }

    @Override
    public void write(Resource r, OutputStream out) throws IOException
    {
        _writer.write(r.getModel(), new OutputStreamWriter(out, CHARSET));
    }
}
