package pt.ist.oai.harvester.exceptions;

public class NoRecordsMatchException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public NoRecordsMatchException(String message) { super(message); }

    @Override
    public String getErrorCode() { return "noRecordsMatch"; }
}