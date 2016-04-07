package pt.ist.oai.harvester.model;

import java.util.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIRecordHeader
{
    protected String             _identifier;
    protected Date               _datestamp;
    protected Collection<String> _setSpecs;
    protected boolean            _deleted;

    public OAIRecordHeader() { this(null, null, null, false); }

    public OAIRecordHeader(String identifier, Date datestamp
                         , Collection<String> setSpecs, boolean deleted)
    {
        _identifier = identifier;
        _datestamp = datestamp;
        _setSpecs = setSpecs;
        _deleted = deleted;
    }

    public String toString()
    {
        return "header[id=" + _identifier + ", datestamp=" + _datestamp + ", setSpecs=" + _setSpecs + "]";
    }

    public Date   getDatestamp()  { return _datestamp; }
    public String getIdentifier() { return _identifier; }

    public Collection<String> getSetSpecs()
    {
        if(_setSpecs == null) return Collections.emptySet();
        else return _setSpecs;
    }

    public boolean hasDatestamp() { return (_datestamp != null); }

    public boolean hasSets()
    {
        return (_setSpecs != null && !_setSpecs.isEmpty());
    }

    public boolean isDeleted() { return _deleted; }

    public void setIdentifier(String identifier) { _identifier = identifier; }
    public void setDatestamp(Date date)          { _datestamp = date; }
    public void setDeleted(boolean deleted)      { _deleted = deleted; }
}
