/**
 * 
 */
package pt.ist.oai.harvester.verb;

import org.apache.http.impl.client.HttpClientBuilder;

import pt.ist.oai.harvester.impl.Identify;
import pt.ist.oai.harvester.model.OAIDataSource;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunIdentity
{
    public static final void main(String[] args) throws Exception
    {
        OAIDataSource source = new Identify(HttpClientBuilder.create())
            .identify("http://bd1.inesc-id.pt:8080/repoxEuDML/OAIHandler?verb=Identify");
        System.err.println("source=" + source);
    }
}
