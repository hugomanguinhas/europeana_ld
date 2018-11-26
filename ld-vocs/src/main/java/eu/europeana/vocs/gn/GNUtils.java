/**
 * 
 */
package eu.europeana.vocs.gn;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 1 May 2017
 */
public class GNUtils
{
    public static Pattern  PATTERN = Pattern.compile("http[:]//sws[.]geonames[.]org/(\\d+)/");

    public static String getId(String uri)
    {
        Matcher m = PATTERN.matcher(uri);
        return ( !m.matches() ? null : m.group(1) );
    }
}
