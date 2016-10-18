/**
 * 
 */
package org.apache.jena.riot;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 31 Aug 2016
 */
public class EDMLang extends Lang
{
    public EDMLang()
    {
        super("EDM/XML"
            , RDFXML.getContentType().getContentType(), RDFXML.getAltNames()
            , RDFXML.getAltContentTypes(), RDFXML.getFileExtensions());
    }

}
