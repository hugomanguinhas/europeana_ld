/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import eu.europeana.ld.edm.EDM;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Apr 2016
 */
public class CompareAgentDumps
{
    public static final void main(String[] args) throws IOException
    {
        Model m1 = ModelFactory.createDefaultModel().read("d:/work/data/entities/agents.xml");
        Model m2 = ModelFactory.createDefaultModel().read("d:/work/data/entities/agents_old.xml");

        ResIterator iter1 = m1.listResourcesWithProperty(RDF.type, EDM.Agent);
        while ( iter1.hasNext() )
        {
            Resource r1  = iter1.next();
            String   uri = r1.getURI();
            Resource r2  = m2.getResource(uri);
            if ( isEmpty(r2) ) {
                System.out.println("Missing resource in m2: " + uri);
                continue;
            }

            if ( compare(r1, r2) ) { continue; }
            System.out.println("Different resource in both models: " + uri);
        }

        ResIterator iter2 = m2.listResourcesWithProperty(RDF.type, EDM.Agent);
        while ( iter2.hasNext() )
        {
            Resource r2  = iter1.next();
            String   uri = r2.getURI();
            Resource r1  = m1.getResource(uri);
            if ( !isEmpty(r1) ) { continue; }

            System.out.println("Missing resource in m1: " + uri);
        }
    }

    protected static boolean compare(Resource r1, Resource r2)
    {
        Model m1 = getNewResource(r1);
        Model m2 = getNewResource(r2);
        return m1.isIsomorphicWith(m2);
    }

    protected static Model getNewResource(Resource r)
    {
        Model model = ModelFactory.createDefaultModel();
        model.add(r.listProperties());
        return model;
    }

    protected static boolean isEmpty(Resource r)
    {
        return !r.hasProperty(RDF.type);
    }
}
