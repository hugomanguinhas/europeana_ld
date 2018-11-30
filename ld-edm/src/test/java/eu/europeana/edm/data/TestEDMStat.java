package eu.europeana.edm.data;

import java.io.File;
import java.io.IOException;

import eu.europeana.ld.edm.analysis.CHOAnalysisOld;

public class TestEDMStat {

    private static File DIR = new File("C:/Users/Hugo/Google Drive/Europeana/Entity Collection/entities/agents/champion_agent/");

    public static final void main(String... args) throws IOException
    {
        File src = new File(DIR, "mozart.xml");
        File dst = new File(DIR, "mozart.stat.txt");
        new CHOAnalysisOld().analyse(src).print(dst);
    }
}