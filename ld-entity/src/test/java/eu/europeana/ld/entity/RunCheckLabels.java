package eu.europeana.ld.entity;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

import static eu.europeana.ld.entity.TestingResources.*;
import static eu.europeana.ld.jena.JenaUtils.*;

public class RunCheckLabels
{
    public static final void main(String[] args)
    {
        PrintStream out = System.out;

        Model m = load(FILE_AGENTS_DBPEDIA_SRC);

        Set<Resource> noLang   = new HashSet();
        Set<Resource> dupLbl   = new HashSet();
        Set<Resource> dupEnLbl = new HashSet();
        ResIterator iter = m.listSubjects();
        while ( iter.hasNext() ) { checkResource(iter.next(), noLang, dupEnLbl, dupLbl); }

        //print
        out.println("Resource with no language: " + noLang.size());
        //for ( Resource rsrc : noLang ) { out.println(rsrc.getURI()); }

        out.println("Resource with duplicate en labels: " + dupEnLbl.size());
        //for ( Resource rsrc : dupEnLbl ) { out.println(rsrc.getURI()); }

        out.println("Resource with duplicate labels in other langs: " + dupLbl.size());
        //for ( Resource rsrc : dupLbl ) { out.println(rsrc.getURI()); }
    }

    private static final void checkResource(Resource rsrc
            , Set<Resource> noLang, Set<Resource> dupEnLbl
            , Set<Resource> dupLbl)
    {
        Map<String,Integer> stat = new HashMap();

        StmtIterator iter = rsrc.listProperties(SKOS.prefLabel);
        while ( iter.hasNext() )
        {
            RDFNode node = iter.next().getObject();
            if ( node.isURIResource() ) { continue; }

            String lang = node.asLiteral().getLanguage();
            if ( lang == null || lang.trim().isEmpty() ) { noLang.add(rsrc); continue; }

            lang = lang.trim();
            Integer i = stat.get(lang);
            stat.put(lang, i == null ? 1 : i + 1);
        }

        for ( String lang : stat.keySet() )
        {
            Integer i = stat.get(lang);
            if ( i <= 1 ) { continue; }

            if ( lang.equals("en") ) { dupEnLbl.add(rsrc); }
            else { dupLbl.add(rsrc); }
        }

        System.out.println(stat);
    }
}
