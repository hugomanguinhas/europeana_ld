/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.jena.rdf.model.Model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public interface ModelWriter
{
    public void write(Model model, OutputStream out) throws IOException;

    public void write(Model model, PrintStream out) throws IOException;

    public void write(Model model, File output) throws IOException;
}
