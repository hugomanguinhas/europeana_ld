package pt.ist.oai.harvester.exceptions;

public class NoSetHierarchyException extends OAIException
{
    private static final long serialVersionUID = 1L;

    public NoSetHierarchyException(String msg) { super(msg); }

    @Override
    public String getErrorCode() { return "noSetHierarchy"; }
}