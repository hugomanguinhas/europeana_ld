package eu.europeana.ld.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

import eu.europeana.ld.analysis.property.DefaultPropertyStat;
import eu.europeana.ld.analysis.property.PropertyStat;
import static eu.europeana.ld.analysis.AnalysisUtil.*;

public class ObjectStat extends AbsStat {

    private String                     _vocName;

    private int                        _total;

    private PropDistributionStat       _propDist;

    private PropDistributionStat       _invPropDist;

    private PropValueDistributionStat  _propValueDist;

    private TypeStat                   _typeStat;

    private LangDistributionStat       _langDist;


    public ObjectStat(String vocName
                    , boolean bPropDist, boolean bInvPropDist
                    , boolean bLangDist)
    {
        _vocName        = vocName;
        _propDist       = bPropDist ? new PropDistributionStat() : null;
        _invPropDist    = bInvPropDist ? new InvPropDistributionStat() : null;
        _typeStat       = new TypeStat(this);
        _propValueDist  = new PropValueDistributionStat();
        _langDist       = bLangDist ? new LangDistributionStat() : null;
    }

    public String getVocName() { return _vocName; }

    public LangDistributionStat getLangStats() { return _langDist; }

    public void addPropertyValue(PropertyStat stat)
    {
        _propValueDist.addPropertyValue(stat);
    }

    public void addPropertyValue(Property p)
    {
        _propValueDist.addPropertyValue(new DefaultPropertyStat(p));
    }

    public void addPropertyValues(Model m)
    {
        addPropertyValues(m, getPropertyURIs(m));
    }

    public void addPropertyValues(Model m, Collection<String> saProp)
    {
        this.addPropertyValues(m, saProp, false);
    }

    public void addPropertyValues(Model m, Collection<String> saProp
                                , boolean inversed)
    {
        for ( String prop : saProp )
        {
            this.addPropertyValue(new DefaultPropertyStat(m.getProperty(prop), inversed));
        }
    }

    public void addPropertyValues(Model m, Property... props)
    {
        addPropertyValues(m, false, props);
    }

    public void addPropertyValues(Model m, boolean inversed, Property... props)
    {
        for ( Property prop : props )
        {
            this.addPropertyValue(new DefaultPropertyStat(prop, inversed));
        }
    }

    public void addPropertyValues(Model m, ResIterator iter)
    {
        addPropertyValues(m, getPropertyURIs(iter));
    }

    public void addPropertyValues(Model m, ResIterator iter, boolean inversed)
    {
        addPropertyValues(m, getPropertyURIs(iter), inversed);
    }


    public PropDistributionStat getPropertyDistributionStat() { return _propDist; }

    public int getTotal() { return _total; }

    public void newObject(Resource r)
    {
        _total++;

        if ( _langDist    != null ) { _langDist.newResource(r);    }
        if ( _propDist    != null ) { _propDist.newResource(r);    }
        if ( _invPropDist != null ) { _invPropDist.newResource(r); }

        _propValueDist.newResource(r);
    }

    public void print(File dst) throws IOException
    {
        print(new PrintStream(dst, "UTF-8"));
    }

    public void print(PrintStream ps)
    {
        printSection(ps, "RESOURCES");
        ps.println();
        ps.println("Total n. of resources: " + _total);
        ps.println();

        if ( _propDist    != null ) { _propDist.print(ps, _total); }
        if ( _langDist    != null ) { _langDist.print(ps, _total); }
        if ( _invPropDist != null ) { _invPropDist.print(ps, _total); }

        _propValueDist.print(ps, _total);
    }

    private void fillType(Resource obj)
    {
        Property type = obj.getModel().getProperty("rdf", "type");
        StmtIterator iter = obj.listProperties(type);
        try {
            while ( iter.hasNext() )
            {
                _typeStat.newType(iter.next().getSubject());
            }
        }
        finally {
            iter.close();
        }
    }
}