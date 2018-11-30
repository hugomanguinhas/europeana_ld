package eu.europeana.ld.entity;

import java.io.File;
import java.io.IOException;

import eu.europeana.ld.edm.analysis.AgentAnalysis;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunAgentStat
{
    private static File DIR = new File("D:/work/data/entities/agents/");

    public static final void main(String... args) throws IOException
    {
        //new AgentAnalysis().analyse(FILE_AGENTS_DBPEDIA);
        new AgentAnalysis().analyse(new File(DIR, "agents.xml"))
                           .print(new File(DIR, "agents.stat.txt"));
    }
}
