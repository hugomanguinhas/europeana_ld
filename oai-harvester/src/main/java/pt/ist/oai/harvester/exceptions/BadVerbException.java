package pt.ist.oai.harvester.exceptions;

public class BadVerbException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public BadVerbException(String msg) { super(msg); }

    @Override
    public String getErrorCode() { return "badVerb"; }
}