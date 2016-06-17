package eu.europeana.ld.edm.analysis;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import eu.europeana.ld.analysis.property.LangFunctionalPropertyStat;
import eu.europeana.ld.analysis.property.LinkSetPropertyStat;
import eu.europeana.ld.edm.EDM;

public class CHOAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);

        stat.addPropertyValue(new LinkSetPropertyStat());
        stat.addPropertyValue(
                new LangFunctionalPropertyStat(DC.language));
        stat.addPropertyValue(
                new LangFunctionalPropertyStat(EDM.language));
        ResIterator iter = m.listSubjectsWithProperty(RDF.type,EDM.ProvidedCHO);
        stat.addPropertyValues(m, iter, false);

        super.analyse(m.listSubjectsWithProperty(RDF.type, EDM.ProvidedCHO)
                                               , stat);

        return stat;
    }
}
