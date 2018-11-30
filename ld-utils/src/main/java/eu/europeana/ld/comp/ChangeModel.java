/**
 * 
 */
package eu.europeana.ld.comp;

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;

import eu.europeana.ld.comp.ChangeModel.ModelChange;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Jan 2017
 */
public class ChangeModel extends ArrayList<ModelChange>
{
    public static enum ChangeType { ADDITION, REMOVAL };

    public static interface ModelChange
    {
        public Statement  getStatement();
        public ChangeType getType();
    }


    private Model _model   = ModelFactory.createDefaultModel();

    public ChangeModel() {}

    public ModelChange newOperation(ChangeType type, Statement stmt)
    {
        if ( type == null ) { return null; }
        switch (type)
        {
            case ADDITION: return newAddition(stmt);
            case REMOVAL : return newRemoval(stmt);
        }
        return null;
    }

    public ModelChange newAddition(Statement stmt)
    {
        ModelChange mc = new AdditionOperation(stmt);
        add(mc);
        return mc;
    }

    public ModelChange newRemoval(Statement stmt)
    {
        ModelChange mc = new RemovalOperation(stmt);
        add(mc);
        return mc;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for ( ModelChange c : this )
        {
            sb.append(c.toString()).append('\n');
        }
        return sb.toString();
    }


    protected abstract static class AtomicOperation implements ModelChange
    {
        protected Statement _stmt;

        public AtomicOperation(Statement stmt) { _stmt = stmt; }

        @Override
        public Statement getStatement() { return _stmt; }
    }
    

    protected static class AdditionOperation extends AtomicOperation
    {
        public AdditionOperation(Statement stmt) { super(stmt); }

        @Override
        public ChangeType getType() { return ChangeType.ADDITION; }

        public String toString() { return "+ " + _stmt.toString(); }
    }

    protected static class RemovalOperation extends AtomicOperation
    {
        public RemovalOperation(Statement stmt) { super(stmt); }

        @Override
        public ChangeType getType() { return ChangeType.REMOVAL; }

        @Override
        public String toString() { return "- " + _stmt.toString(); }
    }
}
