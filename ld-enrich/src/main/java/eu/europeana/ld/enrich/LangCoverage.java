/**
 * 
 */
package eu.europeana.ld.enrich;

import java.util.TreeMap;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.edm.lang.EuropeanaLang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Aug 2017
 */
public class LangCoverage extends TreeMap<String,Integer>
{
    public static final String OTHER = "und";

    public LangCoverage()
    {
        for ( String lang : EuropeanaLang.getLanguages() ) { put(lang, 0); }
        put(OTHER, 0);
    }

    public void add(LangCoverage l)
    {
        for ( String key : keySet() ) { put(key, get(key) + l.get(key)); }
    }

    public void multiply(int value)
    {
        for ( String key : keySet() ) { put(key, get(key) * value); }
    }

    public void clear()
    {
        for ( String key : keySet() ) { put(key, 0); }
    }

    public void analyse(Resource r)
    {
        analyse(r, SKOS.prefLabel);
        analyse(r, SKOS.altLabel);
    }

    public void analyse(Resource r, Property p)
    {
        StmtIterator iter = r.listProperties(p);
        while ( iter.hasNext() )
        {
            RDFNode node = iter.next().getObject();
            if ( !node.isLiteral() ) { continue; }

            String lang = node.asLiteral().getLanguage().trim();
            if ( lang.isEmpty()    ) { continue; }

            if ( !containsKey(lang) ) { lang = OTHER; }
            put(lang, 1);
        }
    }
}