/**
 * 
 */
package eu.europeana.vocs.wikidata;

import static eu.europeana.vocs.VocsUtils.loadModelFromSPARQL;
import static eu.europeana.vocs.isni.ISNIUtils.toURI;
import static eu.europeana.vocs.isni.ZohoOrgUtils.SPARQL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 16 Mar 2018
 */
public class WikidataPropertyLabels
{

    public void addLabels(File src, File dst) throws IOException
    {
        CSVParser  parser  = new CSVParser(new FileReader(src), CSVFormat.EXCEL);
        CSVPrinter printer = new CSVPrinter(new FileWriter(dst), CSVFormat.EXCEL);
        try
        {
            for ( CSVRecord r : parser)
            {
                String wkd  = r.get(0).trim();
                if ( wkd.isEmpty() || !wkd.startsWith("http://") ) { continue; }

                printer.print(wkd);
                printer.print(WikidataUtils.getLabel(wkd, SPARQL));
                int size = r.size();
                for ( int i = 1; i < size; i++ ) { printer.print(r.get(i)); }
                printer.println();
            }
            printer.flush();
        }
        finally { IOUtils.closeQuietly(parser); IOUtils.closeQuietly(printer); }
    }

    public static final void main(String[] args) throws IOException
    {
        File src  = new File("D:\\work\\incoming\\organizations\\wikidata_org.props.csv");
        File dst  = new File("D:\\work\\incoming\\organizations\\wikidata_org.props.labels.csv");
        new WikidataPropertyLabels().addLabels(src, dst);
    }
}
