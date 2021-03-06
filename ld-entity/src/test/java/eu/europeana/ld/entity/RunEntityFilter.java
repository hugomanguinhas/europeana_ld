package eu.europeana.ld.entity;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.entity.filter.EntityFilter;
import static eu.europeana.ld.entity.TestingResources.*;
import static eu.europeana.ld.jena.JenaUtils.*;

public class RunEntityFilter
{
    public static final void main(String... args) throws IOException
    {
        EntityFilter f = new EntityFilter();

        Model m = load(FILE_AGENTS_DBPEDIA_SRC);
        System.out.println("loaded!");

        Collection<String> filtered = f.identifyMatches(m, new TreeSet<String>());
        System.out.println("filtered!");

        PrintStream out = new PrintStream(FILE_AGENTS_DBPEDIA_OUT_CSV);
        for ( String str : filtered ) { out.println(str); }
        out.flush(); out.close();
        System.out.println("Filtered URIs stored!");

        f.filterIn(m, FILE_AGENTS_DBPEDIA_OUT, filtered);
        System.out.println("Filtered Resources stored!");

        f.filterOut(m, FILE_AGENTS_DBPEDIA, filtered);
        System.out.println("New file stored!");
    }
}
