package pt.ist.oai.harvester.exceptions;

public class BadArgumentException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public BadArgumentException(String message) { super(message); }

    @Override
    public String getErrorCode() { return "badArgument"; }
}