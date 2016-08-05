/**
 * 
 */
package eu.europeana.ld.edm;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;

import org.apache.jena.rdf.model.Resource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 15 Apr 2016
 */
public class SVCS
{
    public static final String PREFIX = "svcs";
    public static final String NS     = "http://rdfs.org/sioc/services#";

    public static final Resource Service = createResource(NS + "Service");
}
