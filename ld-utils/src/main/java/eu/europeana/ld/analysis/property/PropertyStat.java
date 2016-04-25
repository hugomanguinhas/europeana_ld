package eu.europeana.ld.analysis.property;

import java.io.PrintStream;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

public interface PropertyStat {

    public Property getProperty();

    public boolean  isInversed();

    public void newPropertyValue(RDFNode node);

    public void print(PrintStream ps, int total);
}
