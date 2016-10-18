/**
 * 
 */
package pt.ist.oai.harvester;

import java.io.File;

import pt.ist.oai.harvester.utils.FileNameHandler;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class RunHarvestToFile
{

    public static final void main(String[] args)
    {
        String file   = "D:\\work\\data\\entities\\mimo.zip";
        String url    = "http://www.mimo-db.eu:8080/oaicat/OAIHandler";
        String set    = "MU";
        String format = "lido";

        OAIHarvestUtils.harvestTo(url, set, format, file);
    }
}
