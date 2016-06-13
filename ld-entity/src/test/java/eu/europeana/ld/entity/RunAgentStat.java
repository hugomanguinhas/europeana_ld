package eu.europeana.ld.entity;

import java.io.File;
import java.io.IOException;

import eu.europeana.ld.edm.analysis.AgentAnalysis;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunAgentStat
{
    private static File DIR = new File("C:/Users/Hugo/Google Drive/Europeana/Entity Collection/entities/agents/champion_agent/");

    public static final void main(String... args) throws IOException
    {
        //new AgentAnalysis().analyse(FILE_AGENTS_DBPEDIA);
        new AgentAnalysis().analyse(new File(DIR, "mozart.xml"))
                           .print(new File(DIR, "mozart.stat.txt"));
    }
}
