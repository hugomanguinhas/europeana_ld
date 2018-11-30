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
import org.apache.commons.io.IOUtils;

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

    public DereferenceChecker(File cache, boolean useHEAD)
    {
        _useHEAD   = useHEAD;
        _cacheFile = initCache(cache);
        _cache     = new TreeMap();
    }

    public DereferenceChecker(boolean useHEAD)
    {
        _useHEAD   = useHEAD;
        _cacheFile = null;
        _cache     = null;
    }

    public Boolean check(String url)
    {
        if ( _cache == null ) { return checkCacheImpl(url); }

        Boolean ret = checkCache(url);
        return ( ret != null ? ret : updateCache(url, checkCacheImpl(url)));
    }

    private HttpMethod newMethod(String url)
    {
        return (_useHEAD ? new HeadMethod(url) : new GetMethod(url));
    }

    private Boolean checkCacheImpl(String url)
    {
        HttpMethod method = newMethod(url);
        try {
            HttpMethodParams params = new HttpMethodParams();
            params.setParameter(HttpMethodParams.RETRY_HANDLER, RETRY_HANDLER);
            method.setParams(params);

            int iRet = _client.executeMethod(method);
            if ( iRet == 200 ) { return true;  }
            if ( iRet == 404 ) { return false; }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally               { method.releaseConnection(); }

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

            if ( _cacheFile == null ) { return value; }
            try                   { _printer.printRecord(url, value);   }
            catch (IOException e) { System.err.println("Error: "
                                                     + e.getMessage()); }
        }
        return value;
    }

    private File initCache(File cache)
    {
        if ( cache == null ) { return null; }

        try                   { adjustCursor(loadCache(cache));   }
        catch (IOException e) { e.printStackTrace(); return null; }

        return cache;
    }

    private File loadCache(File cache) throws IOException
    {
        if ( !cache.exists() ) { return cache; }

        CSVParser  parser = null;
        try {
            parser = CSVParser.parse(cache, _charset, _format);
            for ( CSVRecord record : parser )
            {
                _cache.put(record.get(0), Boolean.parseBoolean(record.get(1)));
            }
        }
        finally { IOUtils.closeQuietly(parser); }

        return cache;
    }

    private void adjustCursor(File cache) throws IOException
    {
        _printer = new CSVPrinter(new PrintStream(cache), _format);
        for ( String url : _cache.keySet() )
        {
            Boolean b = _cache.get(url);
            if ( b == null ) { continue; }

            _printer.printRecord(url, Boolean.toString(b));
        }
    }
}
