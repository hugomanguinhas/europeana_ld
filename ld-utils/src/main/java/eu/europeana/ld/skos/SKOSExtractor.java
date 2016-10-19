/**
 * 
 */
package eu.europeana.ld.skos;

import org.apache.jena.rdf.model.Model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 6 May 2016
 */
public interface SKOSExtractor<O>
{
    public void extract(O src, Model trg);
}
