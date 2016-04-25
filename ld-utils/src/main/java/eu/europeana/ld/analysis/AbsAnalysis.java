package eu.europeana.ld.analysis;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;

import static eu.europeana.ld.jena.JenaUtils.*;

public abstract class AbsAnalysis implements Analysis
{
    public ObjectStat analyse(File src) throws IOException
    {
        return analyse(load(src));
    }

    protected ObjectStat analyse(File src
                               , ObjectStat stat) throws IOException
    {
        return analyse(load(src), stat);
    }

    protected ObjectStat analyse(Model m
                               , ObjectStat stat) throws IOException
    {
        return analyse(m.listSubjects(), stat);
    }

    protected ObjectStat analyse(ResIterator iter
                               , ObjectStat stat) throws IOException
    {
        while ( iter.hasNext() ) { stat.newObject(iter.next()); }

        return stat;
    }

    protected File getDestination(File src) throws IOException
    {
        String name = src.getName().replace(".xml", "") + ".txt";
        return new File(src.getParentFile(), name);
    }

    protected File getDestination(File src, File dst) throws IOException
    {
        return (dst == null ? getDestination(src) : dst);
    }
}
