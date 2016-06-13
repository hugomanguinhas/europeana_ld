package eu.europeana.ld.enrich.disamb;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;

public class SameAsDisambiguator implements ResourceDisambiguator
{
    public SameAsDisambiguator() {}

    @Override
    public int compare(Resource r1, Resource r2)
    {
        int i = countSameAs(r2) - countSameAs(r1);
        if ( i == 0 ) { return r1.getURI().compareTo(r2.getURI()); }
        return i;
    }

    private int countSameAs(Resource r)
    {
        int i = 0;
        StmtIterator iter = r.listProperties(OWL.sameAs);
        while ( iter.hasNext()  ) { iter.next(); i++; }
        return i;
    }
}
