/**
 * 
 */
package eu.europeana.ld.mongo.compare;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import eu.europeana.ld.comp.ChangeModel;
import eu.europeana.ld.comp.ModelDifferenceCalculator;
import eu.europeana.ld.mongo.MongoEDMHarvester;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 13 Jan 2017
 */
public class MongoDifferencePerRecord
{
    private MongoEDMHarvester _s1;
    private MongoEDMHarvester _s2;
    private ModelDifferenceCalculator _calculator
        = new ModelDifferenceCalculator();

    public MongoDifferencePerRecord(MongoClient c1, MongoClient c2, String dbn)
    {
        _s1 = new MongoEDMHarvester(c1, c1.getDatabase(dbn), null, false);
        _s2 = new MongoEDMHarvester(c2, c2.getDatabase(dbn), null, false);
    }

    @Override
    public void finalize()
    {
        _s1.close();
        _s2.close();
    }

    public void compare()
    {
        String id = "http://data.europeana.eu/item/2048005/Athena_Plus_ProvidedCHO_Nationalmuseum__Sweden__Inv__Nr__NMGu_9912___";
        Resource r1 = _s1.harvest(id);
        Resource r2 = _s2.harvest(id);
        ChangeModel cm = compare(r1 == null ? null : r1.getModel()
                               , r2 == null ? null : r2.getModel());
    }

    protected ChangeModel compare(Model m1, Model m2)
    {
        System.out.print("[" + m1.size());
        System.out.print("," + m2.size());
        System.out.println("," + m2.containsAll(m1));

        ChangeModel m = _calculator.computeDiff(m1, m2);
        System.out.print(m);
        return m;
    }

    //koen@knutt.com

    public static final void main(String[] args)
    {
        MongoClient c1 = new MongoClient("144.76.218.178"    , 27017);
        MongoClient c2 = new MongoClient("mongo1.eanadev.org", 27017);
        MongoDifferencePerRecord c = new MongoDifferencePerRecord(c1, c2, "europeana");
        c.compare();
        c.finalize();
    }
}
