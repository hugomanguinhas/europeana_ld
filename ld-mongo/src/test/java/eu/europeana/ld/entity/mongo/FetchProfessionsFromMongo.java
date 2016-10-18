/**
 * 
 */
package eu.europeana.ld.entity.mongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import eu.europeana.ld.deref.DereferencerImpl;
import eu.europeana.ld.deref.HttpDereferencerImpl;
import eu.europeana.ld.edm.EDM;
import eu.europeana.ld.harvester.LDDerefHarvester;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.skos.RDF2SKOSExtractor;
import eu.europeana.ld.store.ZipLDStore;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 May 2016
 */
public class FetchProfessionsFromMongo
{

    public static final void main(String[] args) throws IOException
    {
        File f1 = new File("D:\\work\\eclipse\\mongo\\src\\test\\resources\\etc\\agent_professions_all.csv");
        File f2 = new File("D:\\work\\eclipse\\mongo\\src\\test\\resources\\etc\\agent_professions.zip");
        File f3 = new File("D:\\work\\eclipse\\mongo\\src\\test\\resources\\etc\\agent_professions.rdf");

        Collection<String> roles = fetchRolesToFile(f1);

        fetchResources(roles, f2);
        Model dst = fetchLabels(roles, f2);

        //Model dst = getLabels(roles);
        JenaUtils.store(dst, f3);
    }

    private static Model fetchLabels(Collection<String> roles, File file)
    {
        Model model = JenaUtils.loadAll(file);
        Model ret = ModelFactory.createDefaultModel();
        ret.setNsPrefixes(EDM.PREFIXES);

        for ( String uri : roles ) 
        {
            fetchLabels(model.getResource(uri), ret);
        }
        return ret;
    }

    /*
    private static Model getLabels(Collection<String> roles)
    {
        int i = 0;
        int l = roles.size();
        Model ret = ModelFactory.createDefaultModel();
        NewDereferencer deref = new NewDereferencer();
        for ( String uri : roles )
        {
            System.out.println("Processing [" + (++i) + " of " + l + "]: "
                             + uri);

            Model model = deref.dereference(uri, "application/rdf+xml");
            if ( model == null ) { continue; }

            fetchLabels(model.getResource(uri), ret);
        }

        return ret;
    }
    */

    private static void fetchLabels(Resource rsrc, Model dst)
    {
        int i = 0;
        String uri = rsrc.getURI();

        StmtIterator iter = rsrc.listProperties(RDFS.label);
        while(iter.hasNext())
        {
            i++;
            Statement stmt = iter.next();
            dst.add(dst.createStatement(rsrc, SKOS.prefLabel, stmt.getObject()));
        }

        Property dboTitle
            = rsrc.getModel().getProperty("http://dbpedia.org/ontology/title");
        iter = rsrc.listProperties(dboTitle);
        while(iter.hasNext())
        {
            i++;
            Statement stmt = iter.next();
            dst.add(dst.createStatement(rsrc, SKOS.prefLabel, stmt.getObject()));
        }

        if ( i == 0 ) { System.out.println("No labels found for: " + uri); }
    }

    private static void storeToCSV(Collection<String> uris, File file)
            throws IOException
    {
        CSVPrinter p = null;
        try {
            p = new CSVPrinter(new PrintStream(file), CSVFormat.EXCEL);
            for ( String uri : uris ) { p.printRecord(uri); }
        }
        finally { IOUtils.closeQuietly(p); }
    }

    private static Collection<String> loadRoles(File file)
    {
        Collection<String> ret = new TreeSet();
        CSVParser parser = null;
        try {
            parser = new CSVParser(new FileReader(file), CSVFormat.EXCEL);
            for ( CSVRecord record : parser )
            {
                ret.add(record.get(0));
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(parser); }

        return ret;
    }

    private static Collection<String> fetchRolesToFile(File file)
            throws IOException
    {
        if ( file.exists() ) { return loadRoles(file); }

        Collection<String> ret = new TreeSet();
        MongoClient   client = new MongoClient("136.243.103.29", 27017);
        MongoDatabase db     = client.getDatabase("annocultor_db");

        MongoCollection<Document> col = db.getCollection("TermList");

        String field = "representation.rdaGr2ProfessionOrOccupation.def";
        MongoCursor<String> iter = col.distinct(field, String.class).iterator();
        try {
            while ( iter.hasNext() )
            {
                String uri = iter.next().trim();

                //if ( uri.contains("__")) { continue; }
                if ( !uri.startsWith("http://dbpedia.org/resource/") ) { continue; }

                ret.add(uri);
            }
        }
        finally { iter.close(); }

        storeToCSV(ret, file);

        return ret;
    }

    private static void fetchResources(Collection<String> roles, File file)
    {
        new LDDerefHarvester(new HttpDereferencerImpl())
            .harvest(roles, new ZipLDStore(file, Lang.TTL));
    }

}
