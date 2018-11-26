package eu.europeana.vocs.wikidata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.analysis.AbsAnalysis;
import eu.europeana.ld.analysis.ObjectStat;

import static eu.europeana.vocs.VocsUtils.*;
import static eu.europeana.vocs.wikidata.Wikidata.*;

public class WikidataAnalysis extends AbsAnalysis
{
    private static Collection<String> IGNORE
        = Arrays.asList(NS_WDS, NS_PSV, NS_PQV, NS_WDATA, NS_WDREF, NS_WDNO
                      , NS_PRV);

    private static Collection<String> PROPERTIES
        = Arrays.asList(RDFS.label.getURI(), SKOS.altLabel.getURI()
                      , "http://schema.org/description");

    private Collection<String> _resources;

    public WikidataAnalysis(Collection<String> resources)
    {
        _resources = resources;
    }

    public WikidataAnalysis(File csvFile)
    {
        this(loadDataURLs(csvFile, null));
    }

    public ObjectStat analyse(Model m) throws IOException
    {
        ObjectStat stat  = new ObjectStat("Wikidata", true, true, true);

        Collection<String> props = new TreeSet();

        StmtIterator iter = m.listStatements();
        while ( iter.hasNext() )
        {
            Statement stmt = iter.nextStatement();
            if ( !_resources.contains(stmt.getSubject().getURI()) ) { continue; }

            String uri = stmt.getPredicate().getURI();
            if ( !ignore(uri) ) { props.add(uri); }
        }

        for ( String uri : props )
        {
            stat.addPropertyValue(m.getProperty(uri));
        }

        for ( String uri : _resources ) { stat.newObject(m.getResource(uri)); }

        return stat;
    }

    private boolean ignore(String uri)
    {
        for ( String ns : IGNORE )
        {
            if ( uri.startsWith(ns) ) { return true; }
        }
        return false;
    }
}
