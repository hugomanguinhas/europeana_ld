package eu.europeana.ld.entity;

import eu.europeana.ld.enrich.disamb.AmbiguityFetch;
import eu.europeana.ld.enrich.disamb.BirthDeathDateDisambiguator;
import static eu.europeana.enrich.EnrichmentConstants.*;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunAgentAmbiguityCheck
{
    public static final void main(String[] args)
    {
        new AmbiguityFetch(FILE_AGENT_ENRICHMENTS_HITS
                         , FILE_AGENT_PORTAL_HITS
                         , new BirthDeathDateDisambiguator()).
            process(FILE_AGENTS_DBPEDIA
                  , FILE_AGENTS_DBPEDIA_AMBIGUITY
                  , FILE_AGENTS_DBPEDIA_CLUSTERS);
    }
}
