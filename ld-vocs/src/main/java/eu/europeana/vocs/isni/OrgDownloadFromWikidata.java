/**
 * 
 */
package eu.europeana.vocs.isni;

import static eu.europeana.vocs.VocsUtils.loadModelFromSPARQL;
import static eu.europeana.vocs.isni.ISNIUtils.toURI;
import static eu.europeana.vocs.isni.ZohoOrgUtils.getWkdLabelsNormalized;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.vocs.wikidata.WikidataAnalysis;

import static eu.europeana.vocs.isni.ZohoOrgUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 16 Mar 2018
 */
public class OrgDownloadFromWikidata
{
    public OrgDownloadFromWikidata() {}

    public void download(File src, File dst, File stat) throws IOException
    {
        Model m = ModelFactory.createDefaultModel();

        Collection<String> col = new ArrayList();
        CSVParser  parser  = new CSVParser(new FileReader(src), CSVFormat.EXCEL);
        try
        {
            for ( CSVRecord record : parser)
            {
                String wkd  = record.get(3).trim();
                if ( wkd.isEmpty() || !wkd.startsWith("http://") ) { continue; }

                col.add(wkd);
                loadModelFromSPARQL(m, wkd, true, SPARQL);
            }
        }
        finally { IOUtils.closeQuietly(parser); }

        JenaUtils.store(m, dst);

        new WikidataAnalysis(col).analyse(m).print(stat);
    }

    public static final void main(String[] args) throws IOException
    {
        File src  = new File("D:\\work\\incoming\\organizations\\zoho2wikidata.csv");
        File dst  = new File("D:\\work\\incoming\\organizations\\wikidata_org.xml");
        File stat = new File("D:\\work\\incoming\\organizations\\wikidata_org.txt");
        new OrgDownloadFromWikidata().download(src, dst, stat);
    }
}
