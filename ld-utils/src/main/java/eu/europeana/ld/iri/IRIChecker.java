/**
 * 
 */
package eu.europeana.ld.iri;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * @see https://www.ietf.org/rfc/rfc3987.txt

 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 28 Oct 2016
 */
public class IRIChecker
{
    private static Charset            UTF8    = Charset.forName("UTF-8");
    public  static Pattern            PATTERN
        = Pattern.compile("^([a-zA-Z][a-zA-Z+-.]*):.*$");
    public  static Collection<String> SCHEMES = loadSchemes();

    private static Collection<String> loadSchemes()
    {
        Collection<String> schemes = new TreeSet<String>();
        try
        {
            URL url = IRIChecker.class.getResource("iri.schemes.cfg");
            CSVParser parser = CSVParser.parse(url, UTF8, CSVFormat.EXCEL);
            int i = 0;
            for ( CSVRecord record : parser.getRecords() )
            {
                if ( i++ == 0 ) { continue; }
                schemes.add(record.get(0));
            }
        }
        catch (IOException e) {}

        return schemes;
    }

    public boolean validate(String iri)
    {
        Matcher m = PATTERN.matcher(iri);
        return ( m.find() ? SCHEMES.contains(m.group(1)) : false );
    }
}
