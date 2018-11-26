/**
 * 
 */
package eu.europeana.vocs.coref;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Jun 2016
 */
public class CoReferenceFetcher
{
    private Pattern _pattern;

    public CoReferenceFetcher(Pattern pattern) { _pattern = pattern; }

    public void fetch(Model model, CSVPrinter printer) throws IOException
    {
        StmtIterator iter = model.listStatements(null, OWL.sameAs
                                                ,(RDFNode)null);
        try {
            while ( iter.hasNext() )
            {
                Statement stmt  = iter.nextStatement();
                String    coref = stmt.getResource().getURI();
                if ( !_pattern.matcher(coref).matches() ) { continue; }

                printer.printRecord(stmt.getSubject().getURI(), coref);
            }
        }
        finally { iter.close(); }
    }

    public void fetch(Model model, File file) throws IOException
    {
        CSVPrinter p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
        try { fetch(model, p); p.flush(); } finally { p.close(); }
    }

    public void fetch(Model model, Map<String,List<String>> map)
    {
        StmtIterator iter = model.listStatements(null, OWL.sameAs, (RDFNode)null);
        try {
            while ( iter.hasNext() )
            {
                Statement stmt  = iter.nextStatement();
                String    coref = stmt.getResource().getURI();
                if ( !_pattern.matcher(coref).matches() ) { continue; }

                String       sub  = stmt.getSubject().getURI();
                List<String> list = map.get(sub);
                if ( list == null ) { list = new ArrayList(1); map.put(sub, list); }

                list.add(coref);
            }
        }
        finally { iter.close(); }
    }
}
