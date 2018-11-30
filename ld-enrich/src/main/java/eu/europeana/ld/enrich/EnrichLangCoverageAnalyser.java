/**
 * 
 */
package eu.europeana.ld.enrich;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import eu.europeana.ld.edm.lang.EuropeanaLang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Nov 2016
 */
public class EnrichLangCoverageAnalyser
{
    private Map<Resource,EnrichLangCoverage> _stats;

    public EnrichLangCoverageAnalyser()
    {
        _stats = new HashMap();
    }

    public void analyse(Resource type, Model entities
                      , Map<String,Integer> enrich)
    {
        EnrichLangCoverage stat = new EnrichLangCoverage();

        LangCoverage coverage = new LangCoverage();
        ResIterator iter = entities.listSubjectsWithProperty(RDF.type, type);
        while ( iter.hasNext() )
        {
            Resource r = iter.next();
            coverage.analyse(r);

            stat.add2Overall(coverage);
            if ( !enrich.containsKey(r.getURI()) ) { continue; }

            stat.add2InUse(coverage);
            coverage.multiply(enrich.get(r.getURI()));
            stat.add2Enrich(coverage);

            coverage.clear();
        }

        _stats.put(type, stat);
    }

    public void print(PrintStream ps) throws IOException
    {
        CSVPrinter p = new CSVPrinter(ps, CSVFormat.EXCEL);
        printHeader(p);
        for ( String l : EuropeanaLang.getLanguages() ) { printLine(l, p); }
        printLine(LangCoverage.OTHER, p);
        printFooter(p);
    }

    private void printHeader(CSVPrinter p) throws IOException
    {
        //First line
        p.print("");
        for ( Resource type : _stats.keySet() )
        {
            p.print(type.getLocalName());
            p.print("");
            p.print("");
        }
        p.println();

        //Second line
        p.print("");
        for ( Resource type : _stats.keySet() )
        {
            p.print("Overal");
            p.print("In Use");
            p.print("Enrichments");
        }
        p.println();
    }

    private void printFooter(CSVPrinter p) throws IOException
    {
        p.print("Total");
        for ( Resource type : _stats.keySet() )
        {
            p.print(_stats.get(type).getTotal());
            p.print("");
            p.print("");
        }
        p.println();
    }

    private void printLine(String lang, CSVPrinter p) throws IOException
    {
        p.print(lang);
        for ( Resource type : _stats.keySet() )
        {
            EnrichLangCoverage coverage = _stats.get(type);
            p.print(coverage.getOverall().get(lang));
            p.print(coverage.getInUse().get(lang));
            p.print(coverage.getEnrich().get(lang));
        }
        p.println();
    }
}
