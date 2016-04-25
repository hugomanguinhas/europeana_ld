package eu.europeana.vocs.gemet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

import com.github.jsonldjava.utils.JsonUtils;

import eu.europeana.ld.deref.Dereferencer;

/**
 * 
 */

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Mar 2016
 */
public class AddLabelsToCSV
{
    private Dereferencer _dereferencer = new Dereferencer();
    private CSVFormat    _format       = CSVFormat.EXCEL;
    private Charset      _charset      = Charset.forName("UTF-8");

    public void run(File src, File trg) throws IOException
    {
        CSVParser  parser  = null;
        CSVPrinter printer = null;
        try {
            parser  = CSVParser.parse(src, _charset, _format);
            printer = new CSVPrinter(new PrintStream(trg), _format);

            Iterator<CSVRecord> iter = parser.iterator();
            if ( iter.hasNext() ) { print(printer, iter.next(), "Label"); }

            while ( iter.hasNext() )
            {
                CSVRecord record = iter.next();
                print(printer, record, getEnglishLabel(record.get(0)));
            }
            printer.flush();
        }
        finally {
            if ( parser  != null ) { parser.close();  }
            if ( printer != null ) { printer.close(); }
        }
    }

    private void print(CSVPrinter printer, CSVRecord record, Object... objs) 
            throws IOException
    {
        for ( Object obj : record ) { printer.print(obj); }
        for ( Object obj : objs   ) { printer.print(obj); }
        printer.println();
    }

    private String getEnglishLabel(String url)
    {
        Model model = null;
        try { model = _dereferencer.dereference(url); }
        catch (HttpException e) { e.printStackTrace(); return ""; }

        StmtIterator iter = model.getResource(url).listProperties(SKOS.prefLabel);
        while ( iter.hasNext() )
        {
            Literal literal = iter.next().getLiteral();
            if ( !"en".equals(literal.getLanguage()) ) { continue; }

            return literal.getString();
        }
        return "";
    }

    public static final void main(String[] args) throws IOException
    {
        File dir = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\entities\\gemet");
        File src = new File(dir, "Gemet in Europeana 15-03-2016.csv");
        File dst = new File(dir, "Gemet in Europeana 15-03-2016_label2.csv");
        new AddLabelsToCSV().run(src, dst);
    }
}
