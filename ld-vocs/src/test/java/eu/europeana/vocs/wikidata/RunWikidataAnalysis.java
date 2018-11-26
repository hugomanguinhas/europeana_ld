package eu.europeana.vocs.wikidata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;

import eu.europeana.ld.jena.JenaUtils;

public class RunWikidataAnalysis
{
    private static File _src 
        = new File("D:\\work\\data\\entities\\agents\\wikidata\\agents_wkd.csv");

    public static final void main(String[] args) throws IOException
    {
        //genCSV(_src);
        File file = _src;
        System.out.println("Processing file: " + file.getAbsolutePath());
        Collection<String> uris = loadURIs(file);

        Model model = loadData(file);
        File dstRPT = getFile(file, ".rpt.txt");
        new WikidataAnalysis(uris).analyse(model).print(dstRPT);
    }

    private static void genCSV(File file) throws IOException
    {
        Collection<String> uris = new TreeSet();
        CSVParser parser = new CSVParser(new FileReader(
                new File("D:\\work\\data\\entities\\agents\\coref_wkd.csv")), CSVFormat.EXCEL);
        for ( CSVRecord record : parser )
        {
            String uri = record.get(1).trim();
            if ( uri.isEmpty() ) { continue; }

            uris.add(uri);
        }
        parser.close();
        
        PrintStream ps = new PrintStream(file);
        for ( String uri : uris ) { ps.println(uri); }
        ps.close();
    }

    private static Model loadData(File csvFile)
    {
        File file;

        file = getFile(csvFile, ".xml");
        if ( file.exists() ) { return JenaUtils.load(file); }

        file = getFile(csvFile, ".ttl");
        if ( file.exists() ) { return JenaUtils.load(file); }

        return JenaUtils.loadAll(getFile(csvFile, ".zip"));
    }

    private static File getFile(File file, String suffix)
    {
        String name = file.getName().replace(".csv", suffix);
        return new File(file.getParentFile(), name);
    }

    public static Collection<String> loadURIs(File src)
    {
        Collection<String> s = new TreeSet<String>();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(src));
    
            String sLine;
            while ( (sLine = r.readLine()) != null )
            {
                sLine = sLine.trim();
                if ( sLine.isEmpty() ) { continue; }

                s.add(sLine);
            }
        }
        catch (IOException e) {}
        finally { IOUtils.closeQuietly(r); }

        return s;
    }
}
