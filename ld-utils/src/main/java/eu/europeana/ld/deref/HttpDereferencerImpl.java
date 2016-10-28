/**
 * 
 */
package eu.europeana.ld.deref;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;

import static org.apache.http.HttpHeaders.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Mar 2016
 */
public class HttpDereferencerImpl implements Dereferencer, ResponseHandler<Model>
{
    private static String     DEFAULT_USER_AGENT = "LD Dereferencer";

    private CloseableHttpClient _client;
    private String              _defaultMime;

    
    /***************************************************************************
     * Constructors
     **************************************************************************/

    public HttpDereferencerImpl(CloseableHttpClient client, String defaultMime)
    {
        _defaultMime = defaultMime;
        _client      = client;
    }

    public HttpDereferencerImpl(CloseableHttpClient client, Lang lang)
    {
        this(client, lang.getContentType().getContentType());
    }

    public HttpDereferencerImpl(CloseableHttpClient client)
    {
        this(client, Lang.RDFXML);
    }

    public HttpDereferencerImpl()
    {
        this(HttpClientBuilder.create().build(), Lang.RDFXML);
    }

    /***************************************************************************
     * Interface Dereferencer
     **************************************************************************/

    public Model dereference(String url)
    {
        return fetch(url, _defaultMime);
    }

    public Model dereference(String url, String mime)
    {
        return fetch(url, mime);
    }


    /***************************************************************************
     * Interface ResponseHandler
     **************************************************************************/

    public Model handleResponse(HttpResponse rsp)
           throws ClientProtocolException, IOException
    {
        return read(rsp, getJenaLang(rsp));
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private Model fetch(String url, String mime)
    {
        HttpGet method = new HttpGet(url);
        if ( mime != null ) { method.setHeader(ACCEPT, mime); }

        try                    { return _client.execute(method, this); }
        catch (IOException e ) { logError(url, e);                     }
        finally                { method.releaseConnection();           }

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

    private Model read(HttpResponse rsp, Lang lang) throws IOException
    {
        Model m  = ModelFactory.createDefaultModel();
        if ( lang == null ) { return m; }

        int code = rsp.getStatusLine().getStatusCode();
        if ( code != 200 ) { return m; }

        InputStream is = getInputStream(rsp);
        try                     { m.read(is, lang.getLabel());        }
        catch (RiotException e) { System.out.println(e.getMessage()); }
        finally                 { IOUtils.closeQuietly(is);           }

        return m;
    }

    private InputStream getInputStream(HttpResponse rsp) throws IOException
    {
        return rsp.getEntity().getContent();

        /*
        Header encoding = method.getResponseHeader(CONTENT_ENCODING);
        if ( encoding == null ) { return is; }

        String value = encoding.getValue().toLowerCase();
        if ( value.equals("gzip")   ) { return new GZIPInputStream(is);    }
        if ( value.equals("deflate")) { return new InflaterInputStream(is);}
        return is;
        */
    }

    private Lang getJenaLang(HttpResponse rsp)
    {
        String mime = getContentMime(rsp);
        return ( mime == null ? null : RDFLanguages.contentTypeToLang(mime) );
    }

    private String getContentMime(HttpResponse rsp)
    {
        Header header = rsp.getFirstHeader(CONTENT_TYPE);
        if ( header == null ) { return null; }

        String mime = header.getValue();
        int i = mime.indexOf(';');
        return (i < 0 ? mime : mime.substring(0, i));
    }
}
