package eu.europeana.ld.edm.analysis;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import eu.europeana.ld.analysis.property.LinkSetPropertyStat;
import eu.europeana.ld.edm.RDAGR2;

public class AgentAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);
        stat.addPropertyValues(m, false, RDAGR2.professionOrOccupation);
        /*
        stat.addPropertyValues(m, false
                         , SKOS_PREF_LABEL, SKOS_ALT_LABEL
                         , RDAGR2_BIBINFO, RDAGR2_DATEOFBIRTH, RDAGR2_DATEOFDEATH
                         , DC_IDENTIFIER, EDM_ISRELATEDTO, EDM_END, EDM_PROFOROCCUPATION);
         */
        stat.addPropertyValue(new LinkSetPropertyStat());

        super.analyse(m, stat);

        return stat;
    }
}
