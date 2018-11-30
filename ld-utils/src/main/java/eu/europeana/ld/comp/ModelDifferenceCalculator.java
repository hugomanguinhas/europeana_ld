/**
 * 
 */
package eu.europeana.ld.comp;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import eu.europeana.ld.comp.ChangeModel.ChangeType;
import static eu.europeana.ld.comp.ChangeModel.ChangeType.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Jan 2017
 */
public class ModelDifferenceCalculator
{
    public ChangeModel computeDiff(Model m1, Model m2)
    {
        return computeDiff(m1, m2, new ChangeModel());
    }

    public ChangeModel computeDiff(Model m1, Model m2, ChangeModel m)
    {
        if ( m1 == null ) { newOperations(m, ADDITION, m2); return m; }
        if ( m2 == null ) { newOperations(m, REMOVAL , m1); return m; }

        newOperations(m, ADDITION, m1.difference(m2));
        newOperations(m, REMOVAL , m2.difference(m1));
        return m;
    }

    public ChangeModel computeDiff(Resource r1, Resource r2)
    {
        return computeDiff(r1, r2, new ChangeModel());
    }

    public ChangeModel computeDiff(Resource r1, Resource r2, ChangeModel m)
    {
        if ( r1 == null ) { newOperations(m, ADDITION, r2); return m; }
        if ( r2 == null ) { newOperations(m, REMOVAL , r1); return m; }

        difference(r1, r2, m, ADDITION);
        difference(r2, r1, m, REMOVAL );
        return m;
    }

    private void newOperations(ChangeModel ret
                             , ChangeType type, Model m)
    {
        newOperations(ret, type, m.listStatements());
    }

    private void newOperations(ChangeModel ret
                             , ChangeType type, Resource r)
    {
        newOperations(ret, type, r.listProperties());
    }

    private void newOperations(ChangeModel ret
                             , ChangeType type, StmtIterator iter)
    {
        try {
            while ( iter.hasNext() ) { ret.newOperation(type, iter.next()); }
        }
        finally { iter.close(); }
    }

    private void difference(Resource r1, Resource r2
                          , ChangeModel cm, ChangeType type)
    {
        StmtIterator iter = r1.listProperties();
        try {
            while ( iter.hasNext() )
            {
                Statement stmt = iter.next();
                if ( r2.getModel().contains(stmt) ) { continue; }

                cm.newOperation(type, stmt);
            }
        }
        finally { iter.close(); }
    }
}
