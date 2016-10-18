/**
 * 
 */
package eu.europeana.ld.mongo.callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.edm.io.EDMXMLResourceWriter;
import eu.europeana.ld.harvester.HarvesterCallback;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class FileCallback implements HarvesterCallback
{
    private EDMXMLResourceWriter _writer;

    public FileCallback(File file) throws FileNotFoundException
    {
        _writer = new EDMXMLResourceWriter(new PrintStream(file));
    }

    @Override
    public void handle(Resource r, Status status)
    {
        try                   { _writer.write(r);    }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void finish() { IOUtils.closeQuietly(_writer); }
}
