package eu.europeana.vocs.freebase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;







import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;
import static eu.europeana.vocs.VocsUtils.*;

public class FreebaseAnalysis extends AbsAnalysis
{
    private static String PROPERTY
        = "http://www.w3.org/2000/01/rdf-schema#label";
    
    private Collection<String> _list;
    
    public FreebaseAnalysis(File srcList)
    {
        _list = loadDataURLs(srcList, PATTERN_FREEBASE);
    }

    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat  stat = new ObjectStat("Freebase", true, false, true);
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
