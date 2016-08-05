package eu.europeana.ld.edm.analysis;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import eu.europeana.ld.analysis.property.LangFunctionalPropertyStat;
import eu.europeana.ld.analysis.property.LinkSetPropertyStat;

import static eu.europeana.ld.edm.EDM.*;

public class CHOAnalysis extends AbsAnalysis
{
    private Resource _type;

    public CHOAnalysis(Resource type) { _type = type; }

    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("Europeana", true, false, true);

        stat.addPropertyValue(new LinkSetPropertyStat());
        stat.addPropertyValue(
                new LangFunctionalPropertyStat(DC.language));
        stat.addPropertyValue(
                new LangFunctionalPropertyStat(language));

        ResIterator iter = null;

        iter = m.listSubjectsWithProperty(RDF.type, _type);
        try     { stat.addPropertyValues(m, iter, false); }
        finally { iter.close();                           }

        iter = m.listSubjectsWithProperty(RDF.type, _type);
        try     { super.analyse(iter, stat); }
        finally { iter.close();              }

        return stat;
    }
}
