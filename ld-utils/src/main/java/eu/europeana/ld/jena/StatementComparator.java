/**
 * 
 */
package eu.europeana.ld.jena;

import java.util.Comparator;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import static eu.europeana.ld.jena.JenaUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 6 Oct 2016
 */
public class StatementComparator implements Comparator<Statement>
{
    /***************************************************************************
     * Interface Comparator
     **************************************************************************/
    @Override
    public int compare(Statement s1, Statement s2)
    {
        int ret = compare(s1.getSubject(), s2.getSubject());
        if ( ret != 0 ) { return ret; }

        ret = compare(s1.getPredicate(), s2.getPredicate());
        return ( ret != 0 ? ret : compare(s1.getObject(), s2.getObject()) );
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/

    private int compare(RDFNode n1, RDFNode n2)
    {
        boolean isl1 = n1.isLiteral();
        boolean isl2 = n2.isLiteral();
        if ( isl1  && isl2  ) { compare(n1.asLiteral() , n2.asLiteral());  }
        if ( !isl1 && !isl2 ) { compare(n1.asResource(), n2.asResource()); }
        return (isl1 ? 1 : -1);
    }

    private int compare(Literal l1, Literal l2)
    {
        return l1.getString().compareTo(l2.getString());
    }

    private int compare(Resource r1, Resource r2)
    {
        return r1.getURI().compareTo(r2.getURI());
    }

    private int compare(Property p1, Property p2)
    {
        if ( p1.equals(p2) ) { return 0; }
        return getQName(p1).compareTo(getQName(p2));
    }
}