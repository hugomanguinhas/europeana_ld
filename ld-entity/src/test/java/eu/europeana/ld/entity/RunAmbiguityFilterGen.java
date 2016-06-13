package eu.europeana.ld.entity;

import eu.europeana.ld.enrich.disamb.AmbiguitySelection;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunAmbiguityFilterGen
{

    public static final void main(String[] args)
    {
        AmbiguitySelection sel = new AmbiguitySelection("N","P");
        sel.selectAmbiguities(FILE_AGENTS_DBPEDIA_AMBIGUITY_ANN
                            , FILE_AGENTS_DBPEDIA_AMBIGUITY_OUT);
        sel.selectAmbiguities(FILE_CONCEPTS_DBPEDIA_AMBIGUITY_ANN
                            , FILE_CONCEPTS_DBPEDIA_AMBIGUITY_OUT);
    }
}
