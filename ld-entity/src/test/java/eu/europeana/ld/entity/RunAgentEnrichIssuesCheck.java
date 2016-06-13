package eu.europeana.ld.entity;

import eu.europeana.ld.entity.analysis.AgentEnrichIssuesAnalyser;
import static eu.europeana.ld.entity.TestingResources.*;

public class RunAgentEnrichIssuesCheck
{
    public static final void main(String[] args)
    {
        new AgentEnrichIssuesAnalyser().analyse(FILE_AGENTS_DBPEDIA);
    }
}
