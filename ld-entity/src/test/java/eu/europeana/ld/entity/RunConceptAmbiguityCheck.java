package eu.europeana.ld.entity;

import eu.europeana.ld.enrich.disamb.AmbiguityFetch;
import eu.europeana.ld.enrich.disamb.SameAsDisambiguator;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunConceptAmbiguityCheck
{
    public static final void main(String[] args)
    {
        new AmbiguityFetch(null, null, new SameAsDisambiguator()).
            process(FILE_CONCEPTS_DBPEDIA, FILE_CONCEPTS_DBPEDIA_AMBIGUITY
                  , FILE_CONCEPTS_DBPEDIA_CLUSTERS);
    }
}
