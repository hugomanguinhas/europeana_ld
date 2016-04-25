package eu.europeana.vocs.eurovoc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import static eu.europeana.vocs.VocsUtils.*;

public class EurovocAnalysis extends AbsAnalysis
{    
    private Collection<String> _list;

    public EurovocAnalysis(File srcList)
    {
        _list = loadDataURLs(srcList, PATTERN_EUROVOC);
    }

    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat  stat = new ObjectStat("Eurovoc", true, false, true);
        stat.addPropertyValue(SKOS.prefLabel);
        stat.addPropertyValue(SKOS.altLabel);

        ResIterator iter = m.listSubjects();
        while ( iter.hasNext() )
        {
            Resource r = iter.next();
            if ( !_list.contains(r.getURI()) ) { continue; }
            stat.newObject(r);
        }

        return stat;
    }
}