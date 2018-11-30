package eu.europeana.ld.entity;

import java.io.File;
import java.io.IOException;

import eu.europeana.ld.edm.analysis.ConceptAnalysis;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunConceptStat
{
    public static final void main(String... args) throws IOException
    {
        File dir = new File("D:\\work\\data\\entities\\");
        new ConceptAnalysis().analyse(new File(dir, "concepts_new.xml"))
                             .print(new File(dir, "concepts_new.txt"));
    }
}
