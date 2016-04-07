package pt.ist.oai.harvester.model;

import java.util.*;

import pt.ist.oai.harvester.exceptions.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public interface OAIRequest<Return>
{
    public OAICmdInfo    getInfo();
    public String        getVerb();
    public Properties    getParameters();
    public String        getRequestURI();
    public OAIDataSource getSource();
    public Return        handle() throws OAIException;
    public boolean       hasInfo();
}
