/**
 * 
 */
package pt.ist.oai.harvester;

import java.io.File;

import pt.ist.oai.harvester.utils.FileNameHandler;
import pt.ist.oai.harvester.utils.OAI2FileHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class OAIHarvestUtils
{
    public static void harvestTo(OAIHarvester harvester, FileNameHandler handler
                               , String set, String format, File file)
    {
        new OAI2FileHarvester(harvester, handler).harvest(set, format, file);
    }

    public static void harvestTo(String url, FileNameHandler handler
                               , String set, String format, File file)
    {
        new OAI2FileHarvester(url, handler).harvest(set, format, file);
    }

    public static void harvestTo(OAIHarvester harvester
                               , String set, String format, File file)
    {
        new OAI2FileHarvester(harvester).harvest(set, format, file);
    }

    public static void harvestTo(String url
                               , String set, String format, File file)
    {
        new OAI2FileHarvester(url).harvest(set, format, file);
    }

    public static void harvestTo(String url
                               , String set, String format, String file)
    {
        new OAI2FileHarvester(url).harvest(set, format, new File(file));
    }
}
