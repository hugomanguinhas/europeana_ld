/**
 * 
 */
package eu.europeana.vocs.wikidata;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 17 Jun 2016
 */
public class LoadTDBWithWikidata
{
    public static final void main(String[] args) throws Throwable
    {
        // Make a TDB-backed dataset
        String dir = "D:\\work\\dbs\\jenatdb\\wikidata";
        String src = "E:\\Europeana\\wikidata\\wikidata-terms.nt";
        Dataset dataset = TDBFactory.createDataset(dir) ;

        //dataset.begin(ReadWrite.WRITE) ;
        Model model = dataset.getDefaultModel();
        model.read(src);
        //dataset.end();
        model.close();
    }
}
