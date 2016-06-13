package eu.europeana.ld.entity;

import java.io.IOException;

import eu.europeana.ld.edm.analysis.ConceptAnalysis;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunConceptStat
{
    public static final void main(String... args) throws IOException
    {
        new ConceptAnalysis().analyse(FILE_CONCEPTS_DBPEDIA);
    }
}
