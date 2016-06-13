/**
 * 
 */
package eu.europeana.ld.entity;

import java.io.File;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Aug 2015
 */
public class RunHarvestDumps
{

    public static final void main(String... args)
    {
        File tmpDir = new File("E:\\Europeana\\dbpedia");
        File src    = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Semantic Enrichment\\target vocs\\dbpedia\\dataset_list.txt");
        DumpHarvester harvester = new DumpHarvester(null);
        harvester.harvestAndStore(src, tmpDir);
    }
}
