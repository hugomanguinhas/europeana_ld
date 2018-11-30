/**
 * 
 */
package eu.europeana.ld.enrich;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Aug 2017
 */
public class RunEnrichLangCoverage
{

    public static final void main(String[] args) throws IOException
    {
        File dir = new File("D:/work/data/entities");

        EnrichLangCoverageAnalyser analyser = new EnrichLangCoverageAnalyser();

//        analyser.analyse(EDM.Agent
//                       , JenaUtils.load(new File(dir, "agents/ec/agents.xml"))
//                       , loadEnrichments(new File(dir, "agents/enrich/edm_agent.facet.csv")));
//        analyser.analyse(EDM.Place
//                       , JenaUtils.load(new File(dir, "places/ec/places.xml"))
//                       , loadEnrichments(new File(dir, "places/enrich/edm_place.facet.csv")));
//        analyser.analyse(SKOS.Concept
//                       , JenaUtils.load(new File(dir, "concepts/ec/concepts.xml"))
//                       , loadEnrichments(new File(dir, "concepts/enrich/skos_concept.facet.csv")));
//        analyser.print(System.out);

//        analyser = new EnrichLangCoverageAnalyser();
//        analyser.analyse(SKOS.Concept
//                       , JenaUtils.load(new File(dir, "concepts/ec/old (without GEMET)/concepts.xml"))
//                       , loadEnrichments(new File(dir, "concepts/enrich/skos_concept.facet.csv")));
//        analyser.print(System.out);

          analyser = new EnrichLangCoverageAnalyser();
          analyser.analyse(EDM.TimeSpan
                         , JenaUtils.load(new File(dir, "timespans/ec/timespans.xml"))
                         , loadEnrichments(new File(dir, "timespans/enrich/edm_timespan.facet.csv")));
          analyser.print(System.out);
    }

    private static Map<String,Integer> loadEnrichments(File file)
            throws IOException
    {
        Map<String,Integer> map = new HashMap();
        CSVParser p = new CSVParser(new FileReader(file), CSVFormat.EXCEL);
        try
        {
            for ( CSVRecord record : p )
            {
                map.put(record.get(0), Integer.parseInt(record.get(1)));
            }
        }
        finally { IOUtils.closeQuietly(p); }
        return map;
    }
}
