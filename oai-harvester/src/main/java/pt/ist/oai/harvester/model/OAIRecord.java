package pt.ist.oai.harvester.model;

import org.w3c.dom.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIRecord
{
    protected OAIRecordHeader _header;
    protected Document        _metadata;
    protected Document        _about;

    public OAIRecord(OAIRecordHeader header, Document metadata, Document about)
    {
        _header = header;
        _metadata = metadata;
        _about = about;
    }

    public OAIRecordHeader getHeader()   { return _header;   }
    public Document        getAbout()    { return _about;    }
    public Document        getMetadata() { return _metadata; }

    public boolean hasAbout()    { return (_about != null);    }
    public boolean hasMetadata() { return (_metadata != null); }

    public String toString()
    {
        return "record[" + _header + ", metadata=" + _metadata + ", about=" + _about + "]";
    }
}
