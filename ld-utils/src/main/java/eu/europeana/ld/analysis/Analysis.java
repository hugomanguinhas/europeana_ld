package eu.europeana.ld.analysis;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;

public interface Analysis 
{
    public ObjectStat analyse(File src)  throws IOException;

    public ObjectStat analyse(Model src) throws IOException;
}
