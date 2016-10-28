/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class DefaultWriter implements ModelWriter
{
    private Lang _format;

    public DefaultWriter(Lang format) { _format = format; }

    @Override
    public void write(Model model, OutputStream out)
    {
        model.write(out, _format.getLabel());
    }

    @Override
    public void write(Model model, PrintStream out) throws IOException
    {
        model.write(out, _format.getLabel());
    }

    @Override
    public void write(Model model, File output) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(output);
        try     { model.write(fos, _format.getLabel()); }
        finally { IOUtils.closeQuietly(fos);            }
    }
}
