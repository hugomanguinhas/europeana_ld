package pt.ist.oai.harvester.exceptions;

public class IDDoesNotExistException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public IDDoesNotExistException(String msg) { super(msg); }

    @Override
    public String getErrorCode() { return "idDoesNotExist"; }
}