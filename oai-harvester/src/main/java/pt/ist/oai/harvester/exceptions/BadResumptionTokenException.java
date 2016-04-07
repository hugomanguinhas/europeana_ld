package pt.ist.oai.harvester.exceptions;

public class BadResumptionTokenException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public BadResumptionTokenException(String msg) { super(msg); }

    @Override
    public String getErrorCode() { return "badResumptionToken"; }
}