/**
 * 
 */
package eu.europeana.ld.deref;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 22 Mar 2016
 */
public class DereferenceChecker
{
    private static DefaultHttpMethodRetryHandler RETRY_HANDLER
        = new DefaultHttpMethodRetryHandler(5, false);
    private static CSVFormat _format  = CSVFormat.EXCEL;
    private static Charset   _charset = Charset.forName("UTF-8");

    private HttpClient          _client = new HttpClient();
    private Map<String,Boolean> _cache  = new TreeMap();
    private File                _cacheFile;
    private CSVPrinter          _printer;
    private boolean             _useHEAD;

    public DereferenceChecker(File cache, boolean useHEAD) throws IOException
    {
        _cacheFile = cache;
        _useHEAD   = useHEAD;
        loadCache();
        adjustCursor();
    }

    public Boolean check(String url)
    {
        Boolean ret = checkCache(url);
        return ( ret != null ? ret : updateCache(url, checkCacheImpl(url)));
    }

    private HttpMethod newMethod(String url)
    {
        return (_useHEAD ? new HeadMethod(url) : new GetMethod(url));
    }

    private Boolean checkCacheImpl(String url)
    {
        try {
            HttpMethod method = newMethod(url);

            HttpMethodParams params = new HttpMethodParams();
            params.setParameter(HttpMethodParams.RETRY_HANDLER, RETRY_HANDLER);
            method.setParams(params);

            int iRet = _client.executeMethod(method);
            if ( iRet == 200 ) { return true;  }
            if ( iRet == 404 ) { return false; }
        }
        catch (IOException e) {}

        return null;
    }

    private Boolean checkCache(String url)
    {
        synchronized (_cache) { return _cache.get(url); }
    }

    private Boolean updateCache(String url, Boolean value)
    {
        if ( value == null ) { return value; }

        synchronized (_cache)
        {
            _cache.put(url, value);
            try                   { _printer.printRecord(url, value);   }
            catch (IOException e) { System.err.println("Error: "
                                                     + e.getMessage()); }
        }
        return value;
    }

    private void loadCache() throws IOException
    {
        if ( !_cacheFile.exists() ) { return; }

        CSVParser  parser = null;
        try {
            parser = CSVParser.parse(_cacheFile, _charset, _format);
            for ( CSVRecord record : parser )
            {
                _cache.put(record.get(0), Boolean.parseBoolean(record.get(1)));
            }
        }
        finally
        {
            if ( parser != null ) { parser.close(); }
        }
    }

    private void adjustCursor() throws IOException
    {
        _printer = new CSVPrinter(new PrintStream(_cacheFile), _format);
        for ( String url : _cache.keySet() )
        {
            Boolean b = _cache.get(url);
            if ( b == null ) { continue; }

            _printer.printRecord(url, Boolean.toString(b));
        }
    }
}
