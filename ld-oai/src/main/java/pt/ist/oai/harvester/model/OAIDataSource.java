package pt.ist.oai.harvester.model;

import java.text.*;
import java.util.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIDataSource
{
    public static enum DeletedRecordType
    {
        NO,
        TRANSIENT,
        PERSISTENT,
    }

    public static enum GranularityType
    {
        //YYYY-MM-DD
        DATE(new SimpleDateFormat("yyyy-MM-dd")), 
        //YYYY-MM-DDThh:mm:ssZ
        DATE_AND_TIME(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        protected SimpleDateFormat _format;

        private GranularityType(SimpleDateFormat format) { _format = format; }

        /*
        public DateFormat getFormat() { return _format; }
        */

        public String formatDate(Date date)
        {
            synchronized (_format) { return _format.format(date); }
        }
    
        public Date parseDate(String txt) throws ParseException
        {
            synchronized (_format) { return _format.parse(txt); }
        }

        public static GranularityType parse(String txt)
        {
            if(txt == null)                        { return null; }
            if(txt.equals("YYYY-MM-DD"))           { return DATE; }
            if(txt.equals("YYYY-MM-DDThh:mm:ssZ")) { return DATE_AND_TIME; }
            return null;
        }

        public String toString() { return _format.toPattern(); }
    }

    protected String            _name;
    protected String            _baseURL;
    protected String            _protocolVersion;
    protected List<String>      _emails;
    protected Date              _earliestDs;
    protected DeletedRecordType _deletedRecord;
    protected GranularityType   _granularity;
    protected List<String>      _compression;

    public OAIDataSource(
           String name, String baseURL, String protocolVersion, 
           List<String> emails, Date earliestDs, DeletedRecordType deletedRecord,
           GranularityType granularity, List<String> compression)
    {
        _name            = name;
        _baseURL         = baseURL;
        _protocolVersion = protocolVersion;
        _emails          = emails;
        _earliestDs      = earliestDs;
        _deletedRecord   = deletedRecord;
        _granularity     = granularity;
        _compression     = compression;
    }

    public String            getName()              { return _name;            }
    public String            getBaseURL()           { return _baseURL;         }
    public String            getProtocolVersion()   { return _protocolVersion; }
    public List<String>      getAdminEmails()       { return _emails;          }
    public Date              getEarliestDatestamp() { return _earliestDs;      }
    public DeletedRecordType getDeletedRecord()     { return _deletedRecord;   }
    public GranularityType   getGranularity()       { return _granularity;     }
    public List<String>      getCompressions()      { return _compression;     }

    public boolean hasAdminEmails()
    {
        return (_emails != null && !_emails.isEmpty());
    }

    public boolean hasCompressions()
    {
        return (_compression != null && !_compression.isEmpty());
    }

    public String toString()
    {
        String earliestDatestamp = _granularity.formatDate(_earliestDs);
        return ("oaiSource[name=" + _name + ", baseURL=" + _baseURL
              + ", protocolVersion=" + _protocolVersion
              + ", emails=" + _emails + ", earliestDatestamp="
              + earliestDatestamp + ", deletedRecord=" + _deletedRecord
              + ", granularity=" + _granularity + ", compression="
              + _compression + "]");
    }

    public void setBaseURL(String baseURL) { _baseURL = baseURL; }
}
