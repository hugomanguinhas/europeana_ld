package eu.europeana.vocs.gemet;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

import com.github.jsonldjava.utils.JsonUtils;

import eu.europeana.ld.deref.DereferencerImpl;

/**
 * 
 */

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Mar 2016
 */
public class GetGemetUsage
{
    private static String QUERY
        = "http://www.europeana.eu/api/v2/search.json?query=skos_concept%3A*gemet*&profile=facets&facet=skos_concept&rows=0&wskey=api2demo";

    private DereferencerImpl _dereferencer = new DereferencerImpl();

    public void run(File file) throws IOException
    {
        Map map = new RecordAPI().search2(QUERY);
        CSVPrinter p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
        try {
            ArrayList<Map> list = (ArrayList<Map>)((Map)((ArrayList)map.get("facets")).get(0)).get("fields");
            for ( Map m : list)
            {
                String  gemet = (String)m.get("label");
                if ( !gemet.contains("gemet") ) { continue; }

                Integer count = (Integer)m.get("count");
                p.printRecord(count, getEnglishLabel(gemet), gemet);
            }
            p.flush();
        }
        finally { p.close(); }
    }

    private String getEnglishLabel(String url)
    {
        Model model = null;
        try { model = _dereferencer.dereference(url); }
        catch (HttpException e) { e.printStackTrace(); return ""; }

        StmtIterator iter = model.getResource(url).listProperties(SKOS.prefLabel);
        while ( iter.hasNext() )
        {
            Literal literal = iter.next().getLiteral();
            if ( !"en".equals(literal.getLanguage()) ) { continue; }
            return literal.getString();
        }
        return "";
    }

    /*
    private Object getKey(Map map, String... args)
    {
        Object cursor = map;
        for ( String arg : args )
        {
            cursor = ((Map)cursor).get(arg);
        }
        return cursor;
    }
    */

    public static final void main(String[] args) throws IOException
    {
        File file = new File("D:\\work\\github\\rd-core\\src\\test\\resources\\etc\\gemet\\gemet.csv");
        new GetGemetUsage().run(file);
    }
}
