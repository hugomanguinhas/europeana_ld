package eu.europeana.ld.entity;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.entity.filter.EntityFilter;
import static eu.europeana.ld.jena.JenaUtils.*;

public class RunEntityFilter_2
{
    public static final void main(String... args) throws IOException
    {
        File dir = new File("d:\\work\\data\\entities");
        EntityFilter f = new EntityFilter();

        Model m = load(new File(dir, "agents_new.xml"));
        System.out.println("loaded!");

        Collection<String> filtered = f.identifyMatches(m, new TreeSet<String>());
        System.out.println("filtered!");

        PrintStream out = new PrintStream(new File(dir, "agents_out.csv"));
        for ( String str : filtered ) { out.println(str); }
        out.flush(); out.close();
        System.out.println("Filtered URIs stored!");

        f.filterIn(m, new File(dir, "agents_out.xml"), filtered);
        System.out.println("Filtered Resources stored!");

        f.filterOut(m, new File(dir, "agents_new_solved.xml"), filtered);
        System.out.println("New file stored!");
    }
}
