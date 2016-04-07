package pt.ist.oai.harvester.exceptions;

public class CannotDisseminateFormatException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public CannotDisseminateFormatException(String msg) { super(msg); }

    @Override
    public String getErrorCode() { return "cannotDisseminateFormat"; }
}