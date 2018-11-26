package eu.europeana.vocs.wikidata;

import static eu.europeana.vocs.VocsUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.deref.HttpDereferencerImpl;
import eu.europeana.ld.harvester.LDDerefHarvester;
import eu.europeana.ld.harvester.LDParalellHarvester;
import eu.europeana.ld.sparql.SPARQLDereferencer;
import eu.europeana.ld.store.ZipLDStore;
import eu.europeana.vocs.wikidata.WikidataFetch;

public class RunWikidataHarvest
{
    public static final void main(String... args) throws IOException
    {
        File src = new File("D:\\work\\git\\Europeana\\ld\\ld-entity\\src\\main\\resources\\etc\\data\\agents\\coref_wkd.csv");
        File dst = new File("D:\\work\\data\\entities\\agents\\agents_wkd.zip");

        Collection<String> uris = getWikidataLinks(src);
        harvest(uris, dst);
    }

    private static Collection<String> getWikidataLinks(File src) throws IOException
    {
        Collection<String> ret = new TreeSet();
        CSVParser parser = new CSVParser(new FileReader(src), CSVFormat.EXCEL);
        for ( CSVRecord record : parser ) { ret.add(fixURI(record.get(1))); }
        return ret;
    }

    private static void harvest(Collection<String> list, File file)
    {
        //SPARQL_WIKIDATA
        String endpoint = "https://query.wikidata.org/sparql";
        new LDParalellHarvester(new SPARQLDereferencer(endpoint))
            .harvest(list, new ZipLDStore(file, Lang.TTL));
    }

    private static String fixURI(String uri)
    {
        return uri.replace("http://wikidata.org/", "http://www.wikidata.org/");
    }
}
