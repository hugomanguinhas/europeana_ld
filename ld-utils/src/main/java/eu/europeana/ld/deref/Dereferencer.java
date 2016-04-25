/**
 * 
 */
package eu.europeana.ld.deref;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

import static org.apache.http.HttpHeaders.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Mar 2016
 */
public class Dereferencer
{
    private static HttpClient CLIENT = new HttpClient();
    private static DefaultHttpMethodRetryHandler RETRY_HANDLER 
        = new DefaultHttpMethodRetryHandler(5, false);
    private static String     DEFAULT_USER_AGENT = "LD Dereferencer";

    private String _defaultMime;

    public Dereferencer(String defaultMime) { _defaultMime = defaultMime; }

    public Dereferencer() { this(Lang.RDFXML.getContentType().toString()); }

    public Model dereference(String url) throws HttpException
    {
        return fetch(CLIENT, url, _defaultMime);
    }

    public Model dereference(String url, String mime) throws HttpException
    {
        return fetch(CLIENT, url, mime);
    }

    private Model fetch(HttpClient client, String url, String mime)
            throws HttpException
    {
        GetMethod method = new GetMethod(url);
        if ( mime != null ) { method.setRequestHeader("Accept", mime); }

        method.setRequestHeader(USER_AGENT     , DEFAULT_USER_AGENT);
        method.setRequestHeader(ACCEPT_ENCODING, "gzip");

        HttpMethodParams params = new HttpMethodParams();
        params.setParameter(HttpMethodParams.RETRY_HANDLER, RETRY_HANDLER);
        method.setParams(params);

        try {
            int iRet = client.executeMethod(method);
            if ( iRet == 200 ) { return read(method, getJenaLang(method)); }
            logError(url, iRet);
        }
        catch (HttpException e) { logError(url, e); throw e; }
        catch (IOException e  )
        {
            logError(url, e);
            throw new HttpException(e.getMessage(), e);
        }
        finally { method.releaseConnection(); }

        return null;
    }

    private void  logError(String url, int ret)
    {
        System.err.println("Could not dereference <" + url + ">"
                         + ", response: " + ret);
    }

    private void  logError(String url, Throwable t)
    {
        System.err.println("Could not dereference <" + url + ">"
                         + ", reason <" + t.getClass().getName() + ">"
                         + ", msg: " + t.getMessage());
    }

    private Model read(HttpMethod method, String lang) throws IOException
    {
        Model m  = ModelFactory.createDefaultModel();
        if ( lang == null ) { return m; }

        InputStream is = getInputStream(method);
        try  {
            m.read(is, lang);
        }
        finally { IOUtils.closeQuietly(is); }

        return m;
    }

    private InputStream getInputStream(HttpMethod method) throws IOException
    {
        InputStream is = method.getResponseBodyAsStream();

        Header encoding = method.getResponseHeader(CONTENT_ENCODING);
        if ( encoding == null ) { return is; }

        String value = encoding.getValue().toLowerCase();
        if ( value.equals("gzip")   ) { return new GZIPInputStream(is);    }
        if ( value.equals("deflate")) { return new InflaterInputStream(is);}
        return is;
    }

    private String getJenaLang(HttpMethod method)
    {
        String mime = getContentMime(method);
        if ( mime == null   ) { return null; }

        Lang lang   = RDFLanguages.contentTypeToLang(mime);
        if ( lang == null   ) { return null; }

        return lang.getLabel();
    }

    private String getContentMime(HttpMethod method)
    {
        Header header = method.getResponseHeader(CONTENT_TYPE);
        if ( header == null ) { return null; }

        String mime = header.getValue();
        int i = mime.indexOf(';');
        return (i < 0 ? mime : mime.substring(0, i));
    }
}
