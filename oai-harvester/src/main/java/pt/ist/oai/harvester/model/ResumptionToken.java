package pt.ist.oai.harvester.model;

import java.util.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class ResumptionToken
{
    protected Date   _expirationDate;
    protected Long   _completeListSize;
    protected Long   _cursor;
    protected String _value;

    public ResumptionToken(String value, Date expirationDate
                         , Long cursor, Long completeListSize)
    {
        _value            = value;
        _expirationDate   = expirationDate;
        _cursor           = cursor;
        _completeListSize = completeListSize;
    }

    public String  getValue()            { return _value; }
    public long    getCompleteListSize() { return _completeListSize; }
    public long    getCursor()           { return _cursor; }
    public Date    getExpirationDate()   { return _expirationDate; }

    public boolean hasCursor()           { return _cursor != null; }
    public boolean hasCompleteListSize() { return (_completeListSize != null); }

    public boolean isComplete()          { return _value == null; }
}
