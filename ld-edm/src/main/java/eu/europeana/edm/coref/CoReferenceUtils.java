/**
 * 
 */
package eu.europeana.edm.coref;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Jun 2017
 */
public class CoReferenceUtils
{
    public static Map<String,String> getCorefAsMap(Resource r)
    {
        return getCorefAsMap(r, new HashMap<String,String>());
    }

    public static Map<String,String> getCorefAsMap(Resource r
                                            , Map<String,String> coref)
    {
        getCoref(r, OWL.sameAs     , coref);
        getCoref(r, SKOS.exactMatch, coref);
        return coref;
    }

    public static Collection<String> getCorefAsList(Resource r)
    {
        return getCorefAsList(r, new ArrayList<String>(10));
    }

    public static Collection<String> getCorefAsList(Resource r
                                                  , Collection<String> coref)
    {
        getCorefAsList(r, OWL.sameAs     , coref);
        getCorefAsList(r, SKOS.exactMatch, coref);
        return coref;
    }

    public static void storeCoref(Map<String,String> map, File dst) 
           throws IOException
    {
         CSVPrinter p = new CSVPrinter(new PrintStream(dst), CSVFormat.EXCEL);
         try {
             for ( String k : map.keySet() ) { p.printRecord(k, map.get(k)); }
         }
         finally { IOUtils.closeQuietly(p); }
    }

    public static Map<String,String> loadCoref(File file, int iKey, int iVal)
           throws IOException
     {
         Map<String,String> map = new TreeMap<String,String>();

         int       col    = Math.max(iKey, iVal);
         CSVParser parser = new CSVParser(new FileReader(file), CSVFormat.EXCEL);
         try {
             for ( CSVRecord record : parser)
             {
                 if ( record.size() <= col ) { continue; }

                 String key   = record.get(iKey).trim();
                 String value = record.get(iVal).trim();
                 if ( key.isEmpty() || value.isEmpty() ) { continue; }

                 map.put(key, value);
             }
         }
         finally { parser.close(); }
         return map;
     }

    private static Map<String,String> getCoref(Resource r, Property p
                                             , Map<String,String> coref)
    {
        String       uri  = r.getURI();
        StmtIterator iter = r.listProperties(p);
        try {
            while ( iter.hasNext() )
            {
                RDFNode node = iter.next().getObject();
                if ( !node.isURIResource() ) { continue; }

                coref.put(node.asResource().getURI(), uri);
            }
        }
        finally { iter.close(); }
        return coref;
    }

    private static Collection<String> getCorefAsList(Resource r, Property p
                                                   , Collection<String> coref)
    {
        StmtIterator iter = r.listProperties(p);
        try {
            while ( iter.hasNext() )
            {
                RDFNode node = iter.next().getObject();
                if ( !node.isURIResource() ) { continue; }

                coref.add(node.asResource().getURI());
            }
        }
        finally { iter.close(); }
        return coref;
    }
}
