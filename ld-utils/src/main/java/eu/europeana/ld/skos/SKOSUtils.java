/**
 * 
 */
package eu.europeana.ld.skos;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 6 May 2016
 */
public class SKOSUtils
{
    private static String PATTERN_IRI_PATH_RESERVED
        = "[ \\:/?#\\[\\]@!$&'()*+,;=]+";

    public static String toIRI(String base, String s)
    {
        return base + s.replaceAll(PATTERN_IRI_PATH_RESERVED, "+");
    }
}
