package eu.europeana.ld.entity.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

import eu.europeana.ld.edm.RDAGR2;
import static eu.europeana.ld.jena.JenaUtils.*;
import static eu.europeana.ld.GlobalUtils.*;


/*
 * 
 * description:
 * http://dbpedia.org/resource/Jana_Cova
 * http://dbpedia.org/resource/Bret_Wolfe
 * http://dbpedia.org/resource/Jean-Daniel_Cadinot
 * http://dbpedia.org/resource/Teagan_Presley
 * 
 * keywords:
 * porn star
 * pornographic
 * 
 * http://dbpedia.org/resource/Benjamin_DeMott
 * "Battling the Hard Man: Notes on Addiction to the Pornography of Violence"
 * I also found fotographers that are focus
 * 
 * photographers
 * http://dbpedia.org/resource/Barbara_Nitke
*/
public class EntityFilter
{
    public static ResourceFilter[] ENTITY_FILTERS = null;

    static {
        Class c = EntityFilter.class;
        ENTITY_FILTERS = new ResourceFilter[]
        {
            new ResourceFilterByURI(c.getResourceAsStream("p_uri.txt"))
          , new ResourceFilterByProperty(RDAGR2.professionOrOccupation
                                       , c.getResourceAsStream("p_profession.txt"))
          , new ResourceFilterByProperty(RDAGR2.biographicalInformation
                                       , c.getResourceAsStream("p_bibinfo.txt"))
        };
    }

    private ResourceFilter[] _filters;


    public EntityFilter()                          { this(ENTITY_FILTERS); }

    public EntityFilter(ResourceFilter... filters) { _filters = filters;   }


    public Collection<String> identifyMatches(File src
                                            , Collection<String> filtered)
    {
        return identifyMatches(load(src), filtered);
    }

    public Collection<String> identifyMatches(Model m
                                            , Collection<String> filtered)
    {
        ResIterator iter = m.listResourcesWithProperty(RDF.type);
        while ( iter.hasNext() )
        {
            Resource rsrc = iter.next();
            if ( filter(rsrc) ) { filtered.add(rsrc.getURI()); }
        }
        return filtered;
    }

    public void filterIn(Model src, File dst, Collection<String> col)
           throws IOException
    {
        Model m = ModelFactory.createDefaultModel();
        store(filterIn(src, importNamespaces(src, m), col), dst);
    }

    public Model filterIn(Model src, Model dst, Collection<String> col)
           throws IOException
    {
        if ( dst == null ) { return dst; }

        for ( String uri : col )
        {
            dst.add(src.getResource(uri).listProperties());
        }
        return dst;
    }

    public void filterOut(Model src, File dst, Collection<String> col)
           throws IOException
    {
        Model m = ModelFactory.createDefaultModel();
        store(filterOut(src, importNamespaces(src, m), col), dst);
    }

    public Model filterOut(Model src, Model dst, Collection<String> col)
           throws IOException
    {
        if ( dst == null ) { return dst; }

        ResIterator iter = src.listResourcesWithProperty(RDF.type);
        while ( iter.hasNext() )
        {
            Resource r = iter.next();
            if ( col.contains(r.getURI()) ) { continue; }

            dst.add(r.listProperties());
        }
        return dst;
    }

    public boolean filter(Resource rsrc)
    {
        for ( ResourceFilter filter : _filters )
        {
            if ( filter.filter(rsrc) ) { return true; }
        }
        return false;
    }


    static interface ResourceFilter
    {
        public boolean filter(Resource rsrc);
    }

    static class ResourceFilterByURI implements ResourceFilter
    {
        public Collection<Pattern> _patterns = null;

        public ResourceFilterByURI(Collection<Pattern> patterns)
        {
            _patterns = patterns;
        }

        public ResourceFilterByURI(InputStream in)
        {
            try {
                List<String> lines = loadLines(in);
                _patterns = new ArrayList<Pattern>(lines.size());
                for ( String line : lines )
                {
                    line = line.trim();
                    if ( line.isEmpty() ) { continue; }

                    line = escapeURI2Pattern(line).replaceAll("XXX", ".*");
                    _patterns.add(Pattern.compile(line));
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        @Override
        public boolean filter(Resource rsrc)
        {
            String uri = rsrc.getURI();
            for ( Pattern p : _patterns )
            {
                if ( p.matcher(uri).matches()) { return true; }
            }
            return false;
        }
    }

    static class ResourceFilterByProperty implements ResourceFilter
    {
        private Property                     _property = null;
        private Collection<LocalizedKeyword> _keywords = null;
        private Collection<String>           _uris     = null;

        public ResourceFilterByProperty(Property prop, InputStream in)
        {
            _property = prop;
            _uris     = new ArrayList();
            _keywords = new ArrayList();
            try {
                for ( String line : loadLines(in) )
                {
                    if ( line.startsWith("http://") ) { _uris.add(line); continue; }

                    String[] sa = line.split("@");
                    String literal = sa[0];
                    String lang    = null;
                    if ( sa.length > 1 ) { literal = sa[0]; }
                    _keywords.add(new LocalizedKeyword(literal, lang));
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        @Override
        public boolean filter(Resource rsrc)
        {
            StmtIterator iter = rsrc.listProperties(_property);
            while ( iter.hasNext() )
            {
                RDFNode obj = iter.next().getObject();
                boolean res = false;
                     if ( obj.isLiteral()     ) { res = filterLiteral(obj.asLiteral());   }
                else if ( obj.isURIResource() ) { res = filterResource(obj.asResource()); }

                if ( res ) { return true; }
            }
            return false;
        }

        private boolean filterResource(Resource rsrc)
        {
            return _uris.contains(rsrc.getURI());
        }

        private boolean filterLiteral(Literal literal)
        {
            for ( LocalizedKeyword keyword : _keywords )
            {
                if ( keyword.check(literal) ) { return true; }
            }
            return false;
        }
    }

    static class LocalizedKeyword
    {
        private String _keyword;
        private String _lang;

        public LocalizedKeyword(String keyword, String lang)
        {
            _keyword = keyword;
            _lang    = lang;
        }

        public boolean check(Literal literal)
        {
            if ( (_lang != null)
              && !_lang.equals(literal.getLanguage()) ) { return false; }

            String str = literal.getString().replaceAll("\\s+", " ")
                                .trim().toLowerCase();
            return str.contains(_keyword);
        }
    }
}
