package pt.ist.oai.harvester.exceptions;

public class NoMetadataFormatsException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public NoMetadataFormatsException(String msg) { super(msg); }

    @Override
    public String getErrorCode() { return "noMetadataFormats"; }
}