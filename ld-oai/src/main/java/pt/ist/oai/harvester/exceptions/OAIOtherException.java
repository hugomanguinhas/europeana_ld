package pt.ist.oai.harvester.exceptions;

public class OAIOtherException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public OAIOtherException(String msg) { super(msg); }

    public OAIOtherException(Throwable cause) { super(cause); }

    @Override
    public String getErrorCode() { return "other"; }
}