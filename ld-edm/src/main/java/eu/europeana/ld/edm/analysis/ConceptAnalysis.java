package eu.europeana.ld.edm.analysis;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import eu.europeana.ld.analysis.property.LinkSetPropertyStat;


public class ConceptAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);
        /*
        stat.addPropertyValues(m, false
                         , SKOS_PREF_LABEL, SKOS_ALT_LABEL
                         , RDAGR2_BIBINFO, RDAGR2_DATEOFBIRTH, RDAGR2_DATEOFDEATH
                         , DC_IDENTIFIER, EDM_ISRELATEDTO, EDM_END, EDM_PROFOROCCUPATION);
         */
        stat.addPropertyValue(new LinkSetPropertyStat(SKOS.exactMatch));

        super.analyse(m, stat);

        return stat;
    }
}
