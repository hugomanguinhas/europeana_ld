/**
 * 
 */
package eu.europeana.vocs.isni;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.vocs.coref.CoReferenceUtils;

import org.apache.commons.text.similarity.LevenshteinDistance;
import static eu.europeana.vocs.isni.ZohoOrgUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Mar 2018
 */
public class OrganizationsDeduplication
{
    private ISNICoReferenceResolver _resolver = new ISNICoReferenceResolver("https://query.wikidata.org/sparql");

    public void process(File orgFile, File placeFile, File dst) throws IOException
    {
        Map<String,String>   orgs   = loadZohoOrgs(orgFile);
        Map<String,Resource> places = loadPlaces(placeFile);

        System.out.println("Deduplication started!");

        Set<Duplicate>     dups   = new TreeSet<Duplicate>();

        for ( String org : orgs.keySet() )
        {
            for ( String place : places.keySet() )
            {
                if ( !isSimilar(org,place) ) { continue; }

                dups.add(new Duplicate(orgs.get(org), org
                                     , places.get(place), place));
            }
        }

        CSVPrinter printer = new CSVPrinter(new FileWriter(dst), CSVFormat.EXCEL);
        try {
            for ( Duplicate dup : dups )
            {
                String geoURI = getGeonamesURI(dup._entity);
                String wkdURI = getWikidataURI(geoURI);
                String isni   = getISNI(wkdURI);
                printer.printRecord(dup._zohoID, dup._entity.getURI()
                                  , wkdURI, isni, geoURI
                                  , dup._zohoLabel, dup._entityLabel);
            }

            printer.flush();
        }
        finally { printer.close(); }
    }

    private Map<String,Resource> loadPlaces(File src) throws IOException
    {
        Model model = JenaUtils.load(src);
        Map<String,Resource> map = new HashMap();
        ResIterator ri = model.listResourcesWithProperty(RDF.type, EDM.Place);
        try
        {
            while ( ri.hasNext() )
            {
                Resource r   = ri.next();
                loadLabels(r, map, SKOS.prefLabel);
                loadLabels(r, map, SKOS.altLabel);
            }
        }
        finally { ri.close(); }

        return map;
    }

    private void loadLabels(Resource r, Map<String,Resource> map, Property p)
    {
        StmtIterator iter = r.listProperties(p);
        try {
            while ( iter.hasNext() )
            {
                map.put(normalize(iter.next().getString()), r);
            }
        }
        finally { iter.close(); }
    }

    private String getGeonamesURI(Resource r)
    {
        StmtIterator iter = r.listProperties(OWL.sameAs);
        while ( iter.hasNext() )
        {
            String uri = iter.next().getResource().getURI();
            if ( uri.startsWith("http://sws.geonames.org/") ) { return uri; }
        }
        return null;
    }

    private String getWikidataURI(String uri)
    {
        if (uri == null) { return null; }

        for ( String wkd : CoReferenceUtils.GN_2_WD.resolve(uri) )
        {
            return wkd;
        }
        return null;
    }

    private String getISNI(String wkd)
    {
        if ( wkd == null ) { return null; }

        for ( String isni : _resolver.resolve(wkd) )
        {
            return isni;
        }
        return null;
    }

    private static class Duplicate implements Comparable<Duplicate>
    {
        private String   _zohoID;
        private Resource _entity;
        private String   _zohoLabel;
        private String   _entityLabel;

        public Duplicate(String zohoID, String zohoLabel
                       , Resource entity, String entityLabel)
        {
            _zohoID      = zohoID;
            _zohoLabel   = zohoLabel;
            _entity      = entity;
            _entityLabel = entityLabel;
        }

        @Override
        public int compareTo(Duplicate dup)
        {
            int comp = _zohoID.compareTo(dup._zohoID);
            return ( comp == 0 ? _entity.getURI().compareTo(dup._entity.getURI()) : comp);
        }
    }

    public static final void main(String[] args) throws IOException
    {
        File orgs   = new File("D:\\work\\incoming\\organizations\\DataProvider.csv");
        File places = new File("D:\\work\\data\\entities\\places\\ec\\places.xml");
        File dst    = new File("D:\\work\\incoming\\organizations\\similar.csv");
        new OrganizationsDeduplication().process(orgs, places, dst);
    }
}
