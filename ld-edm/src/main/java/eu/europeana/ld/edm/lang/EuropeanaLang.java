/**
 * 
 */
package eu.europeana.ld.edm.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Nov 2016
 */
public class EuropeanaLang
{
    private static final Collection<String> LANGUAGES = new TreeSet();

    static {
        Class          c  = EuropeanaLang.class;
        InputStream    is = c.getClassLoader().getSystemResourceAsStream("etc/lang/europeana.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            while ( br.ready() )
            {
                String lang = br.readLine().trim();
                if ( lang.isEmpty() ) { continue; }

                LANGUAGES.add(lang);
            }
        }
        catch(IOException e) { e.printStackTrace();      } 
        finally              { IOUtils.closeQuietly(br); }
    }

    public static Collection<String> getLanguages() { return LANGUAGES; }

    public static boolean isSupported(String lang)
    {
        return LANGUAGES.contains(lang);
    }
}
