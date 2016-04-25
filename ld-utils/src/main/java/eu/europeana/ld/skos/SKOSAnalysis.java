package eu.europeana.ld.skos;

import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import eu.europeana.ld.analysis.property.LinkSetPropertyStat;

public class SKOSAnalysis extends AbsAnalysis
{
    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("SKOS", true, false, true);

        stat.addPropertyValue(new LinkSetPropertyStat());
        ResIterator iter = m.listSubjectsWithProperty(RDF.type, SKOS.Concept);
        stat.addPropertyValues(m, iter, false);

        super.analyse(m.listSubjectsWithProperty(RDF.type, SKOS.Concept), stat);

        return stat;
    }
}
