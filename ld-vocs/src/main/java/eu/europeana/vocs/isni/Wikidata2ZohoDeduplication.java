/**
 * 
 */
package eu.europeana.vocs.isni;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import static eu.europeana.vocs.isni.ZohoOrgUtils.*;
import static eu.europeana.vocs.isni.ISNIUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 10 Mar 2018
 */
public class Wikidata2ZohoDeduplication
{
    public void generate(File srcZoho, File srcWkd, File dst) throws IOException
    {
        Map<String,String> zoho = loadZohoOrgs(srcZoho);
        CSVParser  parser  = new CSVParser(new FileReader(srcWkd), CSVFormat.EXCEL);
        CSVPrinter printer = new CSVPrinter(new FileWriter(dst), CSVFormat.EXCEL);

        Set<String> labels = new TreeSet();
        try
        {
            for ( CSVRecord record : parser)
            {
                String wkd  = record.get(0).trim();
                String isni = record.get(1).trim();

                String[] match  = lookup(zoho, getWkdLabelsNormalized(wkd, labels));
                if ( match == null ) { continue; }

                String zohoID = zoho.get(match[0]);
                printer.printRecord(zohoID, match[0], match[1], wkd, toURI(isni));

                labels.clear();
            }
            printer.flush();
        }
        finally { parser.close(); printer.close(); }
    }

    private String[] lookup(Map<String,String> zoho, Set<String> labels)
    {
        //using equals
        for ( String l1 : zoho.keySet() )
        {
            for ( String l2 : labels )
            {
                if ( l1.equals(l2) ) { return new String[] { l1, l2 }; }
            }
        }

        //using similarity
        for ( String l1 : zoho.keySet() )
        {
            for ( String l2 : labels )
            {
                if ( isSimilar(l1, l2) ) { return new String[] { l1, l2 }; }
            }
        }

        return null;
    }


    public static final void main(String[] args) throws IOException
    {
        if ( args.length < 3 ) { return; }

        //DataProvider.csv
        File srcZoho = new File(args[0]);
        //wikidata_glams.csv
        File srcWkd = new File(args[1]);
        //zoho2wikidata.csv
        File dst = new File(args[2]);
        new Wikidata2ZohoDeduplication().generate(srcZoho, srcWkd, dst);
    }
}
