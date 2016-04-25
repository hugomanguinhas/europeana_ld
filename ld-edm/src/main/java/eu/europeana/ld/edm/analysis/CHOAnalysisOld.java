package eu.europeana.ld.edm.analysis;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.vocabulary.RDF;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import eu.europeana.ld.edm.EDM;

public class CHOAnalysisOld extends AbsAnalysis
{
    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);
        //stat.addPropertyValue(new LinkSetPropertyStat(m));

        ResIterator iter = m.listResourcesWithProperty(RDF.type
                                                     , EDM.ProvidedCHO);
        super.analyse(iter, stat);

        return stat;
    }
}
