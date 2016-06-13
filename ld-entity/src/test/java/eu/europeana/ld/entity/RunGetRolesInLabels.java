package eu.europeana.ld.entity;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;

import static eu.europeana.ld.jena.JenaUtils.*;

public class RunGetRolesInLabels
{
    private static Pattern  PATTERN  = Pattern.compile(".+\\((.+)\\).*");
    private static File DIR = new File("C:\\Users\\Hugo\\Google Drive\\Europeana\\Entity Collection\\entities\\agents");

    public static final void main(String[] args)
    {
        File src = new File(DIR, "agents_dbpedia_full.xml");
        Model m = load(src);
        Collection<String> roles = new TreeSet();

        ResIterator iter = m.listResourcesWithProperty(RDF.type);
        //while ( iter.hasNext() ) { getRolesInLabel(iter.next(), roles); }
        while ( iter.hasNext() ) { getRolesInResource(iter.next(), roles); }

        for ( String role : roles ) { System.out.println(role); }
    }

    private static void getRolesInResource(Resource rsrc, Collection<String> roles)
    {
        getRole(rsrc.getURI(), roles);
    }

    private static void getRolesInLabel(Resource rsrc, Collection<String> roles)
    {
        Model    m = rsrc.getModel();
        StmtIterator iter = rsrc.listProperties(SKOS.prefLabel);
        while ( iter.hasNext() )
        {
            Statement stmt = iter.next();
            if ( !stmt.getObject().isLiteral() ) { continue; }

            getRole(stmt.getString(), roles);
        }
    }

    private static void getRole(String label, Collection<String> roles)
    {
        Matcher m = PATTERN.matcher(label);
        if ( !m.matches() ) { return; }

        roles.add(m.group(1).toLowerCase().trim());
    }
}
