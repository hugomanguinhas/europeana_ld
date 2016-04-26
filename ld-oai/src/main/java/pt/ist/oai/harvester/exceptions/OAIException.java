package pt.ist.oai.harvester.exceptions;

import java.util.*;

public abstract class OAIException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    protected static Map<String,Class<? extends OAIException>> _exceptions = 
        new HashMap<String,Class<? extends OAIException>>();

    static
    {
        _exceptions.put("badArgument", BadArgumentException.class);
        _exceptions.put("badResumptionToken", BadResumptionTokenException.class);
        _exceptions.put("badVerb", BadVerbException.class);
        _exceptions.put("cannotDisseminateFormat", CannotDisseminateFormatException.class);
        _exceptions.put("idDoesNotExist", IDDoesNotExistException.class);
        _exceptions.put("noMetadataFormats", NoMetadataFormatsException.class);
        _exceptions.put("noRecordsMatch", NoRecordsMatchException.class);
        _exceptions.put("noSetHierarchy", NoSetHierarchyException.class);
    }

    public static OAIException getException(String errorCode)
    {
        try                { return _exceptions.get(errorCode).newInstance(); }
        catch(Exception e) { return new OAIOtherException(errorCode);         }
    }
    
    public static OAIException getException(String errorCode, String msg)
    {
        if(msg == null) { return getException(errorCode); }
        try {
            return _exceptions.get(errorCode).getConstructor(String.class)
                              .newInstance(msg);
        }
        catch(Exception e) { return new OAIOtherException(msg); }
    }

    public OAIException()                { super(); }

    public OAIException(String msg)      { super(msg); }

    public OAIException(Throwable cause) { super(cause); }

    public abstract String getErrorCode();
}