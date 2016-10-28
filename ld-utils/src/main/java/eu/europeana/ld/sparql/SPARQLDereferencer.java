/**
 * 
 */
package eu.europeana.ld.sparql;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

import eu.europeana.ld.deref.Dereferencer;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class SPARQLDereferencer implements Dereferencer
{
    private static String QUERY = "DESCRIBE <?>";

    private String _endpoint;

    public SPARQLDereferencer(String endpoint) { _endpoint = endpoint; }

    @Override
    public Model dereference(String uri) throws IOException
    {
        String          query    = getQuery(uri);
        QueryEngineHTTP endpoint = new QueryEngineHTTP(_endpoint, query);
        try {
            return endpoint.execDescribe(ModelFactory.createDefaultModel());
        }
        catch (Throwable t) { t.printStackTrace(); return null; }
        finally { endpoint.close(); }
    }

    @Override
    public Model dereference(String uri, String mime) throws IOException
    {
        return dereference(uri);
    }

    private String getQuery(String uri) { return QUERY.replace("?", uri); }
}
