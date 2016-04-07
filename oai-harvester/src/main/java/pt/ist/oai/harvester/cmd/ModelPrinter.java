package pt.ist.oai.harvester.cmd;

import java.io.*;
import java.util.Date;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.Document;

import pt.ist.oai.harvester.model.*;

public class ModelPrinter
{
    protected OAIDataSource _ds;

    public ModelPrinter() { this(null); }

    public ModelPrinter(OAIDataSource ds) { _ds = ds; }

    protected String print(String v) { return (v == null ? "" : v); }

    protected String getDate(Date date)
    {
        if (date == null) { return "?"; }
        if (_ds != null)  { return _ds.getGranularity().formatDate(date); }
        return date.toString();
    }

    public void print(OAIDataSource ds, PrintStream ps)
    {
        String delRecord = ds.getDeletedRecord().name().toLowerCase();
        ps.println("              name: " + ds.getName());
        ps.println("               url: " + ds.getBaseURL());
        ps.println("  protocol version: " + ds.getProtocolVersion());
        ps.println("    admin contacts: " + ds.getAdminEmails());
        ps.println("earliest datestamp: " + ds.getEarliestDatestamp());
        ps.println("    deleted record: " + delRecord);
        ps.println("       granularity: " + ds.getGranularity());
        ps.println("       compression: " + ds.getCompressions());
    }

    public void print(OAIMetadataFormat mf, PrintStream ps)
    {
        ps.println("            prefix: " + print(mf.getMetadataPrefix()));
        ps.println("         namespace: " + print(mf.getMetadataNamespace()));
        ps.println("   schema location: " + print(mf.getSchemaLocation()));
    }

    public void print(OAIMetadataSet set, PrintStream ps)
    {
        ps.println("              name: " + print(set.getName()));
        ps.println("       description: " + print(set.getDescription()));
        ps.println("          set spec: " + print(set.getSetSpec()));
    }

    public void print(OAIRecordHeader header, PrintStream ps)
    {
        String setSpecs = header.getSetSpecs().toString();
        ps.println("        identifier: " + print(header.getIdentifier()));
        if(header.isDeleted()) { ps.println("            status: deleted"); }
        ps.println("         datestamp: " + getDate(header.getDatestamp()));
        ps.println("         set specs: " + print(setSpecs));
    }

    public void print(OAIRecord record, PrintStream ps)
    {
        print(record.getHeader(), ps);
    }

    public void print(Document doc, PrintStream ps)
    {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.transform(new DOMSource(doc), new StreamResult(ps));
        }
        catch (TransformerConfigurationException e) {
        }
        catch (TransformerException e) {
        }
        catch (TransformerFactoryConfigurationError e) {
        }
    }

    public void printAsXML(OAIRecord record, PrintStream p) {}
}
