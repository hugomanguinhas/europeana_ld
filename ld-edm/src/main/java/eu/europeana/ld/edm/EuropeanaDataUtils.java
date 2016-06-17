package eu.europeana.ld.edm;

public class EuropeanaDataUtils
{
    public static final String NS     = "http://data.europeana.eu/";
    public static final String CHO_NS = NS + "item/";

    public String getURIforCHO(String localID) { return CHO_NS + localID; }
}
