package eu.europeana.vocs.babelnet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import static eu.europeana.vocs.VocsUtils.*;

public class BabelNetAnalysis extends AbsAnalysis
{
    private static String PROPERTY
        = "http://babelnet.org/model/babelnet#gloss";

    private Collection<String> _list;

    public BabelNetAnalysis(File srcList)
    {
        _list = loadDataURLs(srcList, PATTERN_BABELNET);
    }

    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat  stat = new ObjectStat("BabelNet", true, false, true);
        stat.addPropertyValue(m.getProperty(PROPERTY));

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
