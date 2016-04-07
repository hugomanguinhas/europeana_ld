package pt.ist.oai.harvester.model;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIMetadataSet
{
    protected String _setSpec;
    protected String _name;
    protected String _description;

    public OAIMetadataSet(String spec, String name, String description)
    {
        _setSpec = spec;
        _name = name;
        _description = description;
    }

    public String getSetSpec()     { return _setSpec; }
    public String getName()        { return _name; }
    public String getDescription() { return _description; }

    public String toString()
    {
        return "set[spec=" + _setSpec + ", name=" + _name + ",description=" + _description + "]";
    }
}