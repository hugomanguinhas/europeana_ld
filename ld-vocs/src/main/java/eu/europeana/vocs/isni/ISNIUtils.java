/**
 * 
 */
package eu.europeana.vocs.isni;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Mar 2018
 */
public class ISNIUtils
{
    private static String PATTERN_ID  = "^[0-9A-Z]{4}( [0-9A-Z]{4}){3}$";
    private static String ISNI_URI = "http://isni.org/isni/";

    public static boolean isISNI(String isni)
    {
        return (isni == null ? false : isni.matches(PATTERN_ID) );
    }

    public static String toURI(String isni)
    {
        String str = isni.replaceAll(" ", "");
        return (str.isEmpty() ? null : ISNI_URI + str);
    }
}
