package pt.ist.oai.harvester.model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIMetadataFormat
{
    protected String _namespace;
    protected String _prefix;
    protected String _schemaLocation;

    public OAIMetadataFormat(String namespace, String prefix
                           , String schemaLocation)
    {
        _namespace = namespace;
        _prefix = prefix;
        _schemaLocation = schemaLocation;
    }

    public String getMetadataNamespace() { return _namespace;      }
    public String getMetadataPrefix()    { return _prefix;         }
    public String getSchemaLocation()    { return _schemaLocation; }

    public String toString()
    {
        return "format[namespace=" + _namespace + ", prefix=" + _prefix + ",schemaLocation=" + _schemaLocation + "]";
    }
}
