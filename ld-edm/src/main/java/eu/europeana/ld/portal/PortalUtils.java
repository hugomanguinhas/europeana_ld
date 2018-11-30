/**
 * 
 */
package eu.europeana.ld.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.europeana.ld.edm.EuropeanaDataUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 25 Nov 2016
 */
public class PortalUtils
{
    private static String  PORTAL_STR
        = "^http://(?:www[.])?europeana[.]eu/+(?:[^/]+/+)+record/+"
        + "(\\d+[a-z]?)/([^/.&?]+)([.].*)?$";
    private static Pattern PORTAL = Pattern.compile(PORTAL_STR);

    public static String getEuropeanaURI(String page)
    {
        Matcher m = PORTAL.matcher(page);
        if ( !m.find() ) { return null; }

        String datasetId = m.group(1);
        String recordId  = m.group(2);
        return EuropeanaDataUtils.getURIforCHO(datasetId, recordId);
    }

    public static final void main(String[] args)
    {
        String[] arg = {
            "http://europeana.eu/portal/record/2020903/KMS4215.html"
          , "http://europeana.eu/portal/full-doc.html?query=tartarin&start=23&startPage=13&uri=http://www.europeana.eu/resolve/record/04031/9D23E5A9F145674FF0FA5F18547481A3432B9BCF&view=table&pageId=bd"
          , "http://europeana.eu/portal/record/09404/BA3782E9B432DC29E8176AFA6BCC64C667E8FE6F.html?start=1&query=starynkiewicza&startPage=1&rows=24"
        };
        for ( String s : arg )
        {
            System.out.println(getEuropeanaURI(s));
        }
    }
}
