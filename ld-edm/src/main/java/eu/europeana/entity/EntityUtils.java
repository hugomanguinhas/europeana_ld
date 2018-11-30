/**
 * 
 */
package eu.europeana.entity;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Jul 2017
 */
public class EntityUtils
{
    public static String getPrefLabel(Resource r, String lang)
    {
        return getLangValue(r, SKOS.prefLabel, lang);
    }

    public static String getOnePrefLabel(Resource r, String defLang)
    {
        return getOneLangValue(r, SKOS.prefLabel, defLang);
    }

    public static String getOneLangValue(Resource r, Property p, String defLang)
    {
        String value = getLangValue(r, p, defLang);
        return ( value == null ? getFirstValue(r, p) : value );
    }

    public static String getLangValue(Resource r, Property p, String lang)
    {
        StmtIterator iter = r.listProperties(p);
        try {
            while ( iter.hasNext() )
            {
                RDFNode node = iter.next().getObject();
                if ( !node.isLiteral() ) { continue; }

                Literal l  = node.asLiteral();
                String  ll = l.getLanguage();
                if ( lang.equals(ll) ) { return l.getString(); }
            }
        }
        finally { iter.close(); }

        return null;
    }

    public static String getFirstValue(Resource r, Property p)
    {
        StmtIterator iter = r.listProperties(p);
        try {
            return ( iter.hasNext() ? iter.next().getLiteral().getString()
                                    : null );
        }
        finally { iter.close(); }
    }
}
