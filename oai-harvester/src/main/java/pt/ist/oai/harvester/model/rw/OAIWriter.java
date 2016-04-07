package pt.ist.oai.harvester.model.rw;

import java.io.*;
import java.util.Date;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.CloseableIterable;
import pt.ist.xml.writer.*;

import static pt.ist.oai.harvester.OAIConstants.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 18 Dec 2015
 */
public class OAIWriter
{
    protected OAIDataSource _source;

    public OAIWriter() {}

    protected void write(OAIRecordHeader header, ContentHandlerWriter handler)
              throws SAXException
    {
        handler.writeStartElementNS(NAMESPACE_URI, "header");
        handler.writeLeafElementNS(NAMESPACE_URI, "identifier", header.getIdentifier());
        if(header.hasDatestamp()) {
            String dateStr = _source.getGranularity().formatDate(header.getDatestamp());
            handler.writeLeafElementNS(NAMESPACE_URI, "datestamp", dateStr);
        }
        if(header.hasSets()) {
            for(String setSpec : header.getSetSpecs())
            {
                handler.writeLeafElementNS(NAMESPACE_URI, "setSpec", setSpec);
            }
        }
        handler.writeEndElement();
    }

    protected void write(String element, Document doc
                       , ContentHandlerWriter handler)
    {
        Element root = doc.getDocumentElement();
        if(root == null) { return; }

        handler.writeStartElementNS(NAMESPACE_URI, element);
        handler.writeNode(root);
        handler.writeEndElement();
    }

    protected void write(OAIRecord record, ContentHandlerWriter handler)
              throws SAXException
    {
        handler.writeStartElementNS(NAMESPACE_URI, "record");
        write(record.getHeader(), handler);
        if(record.hasMetadata()) write("metadata", record.getMetadata(), handler);
        if(record.hasAbout()) write("about", record.getAbout(), handler);
        handler.writeEndElement();
    }

    protected void writeRequest(OAIRequest<?> request, String verb
                              , ContentHandlerWriter handler)
    {
        handler.writeAttributeNS(NAMESPACE_URI, "verb", verb);
        for(Map.Entry<Object,Object> entry : request.getParameters().entrySet())
        {
            handler.writeAttributeNS(NAMESPACE_URI, entry.getKey().toString(), entry.getValue().toString());
        }
        handler.writeEmptyElementNS(NAMESPACE_URI, "request");
    }

    public synchronized void write(OAIRecord record, OAIDataSource source, Result result) throws IOException {
        try {
            _source = source;
            ContentHandlerWriter handler = new ContentHandlerWriter(result);
            handler.startPrefixMapping(NAMESPACE_PREFIX, NAMESPACE_URI);
            handler.writeStartDocument();
            write(record, handler);
            handler.writeEndDocument();
        }
        catch(TransformerConfigurationException | SAXException e)
        {
            throw new IOException(e);
        }
        finally { _source = null; }
    }

    public synchronized long write(OAIRequest<CloseableIterable<OAIRecord>> request, Date responseDate, Result result) throws IOException {
        CloseableIterable<OAIRecord> iterable = request.handle();
        try {
            _source = request.getSource();
            long count = 0;
            ContentHandlerWriter handler = new ContentHandlerWriter(result);
            handler.startPrefixMapping(NAMESPACE_PREFIX, NAMESPACE_URI);
            handler.startPrefixMapping("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            handler.writeStartDocument();
            handler.writeAttributeNS(
                    XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation", 
                    "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd");
            handler.writeStartElementNS(NAMESPACE_URI, "OAI-PMH");
            if(responseDate == null) responseDate = new Date();
            handler.writeLeafElementNS(
                    NAMESPACE_URI, "responseDate", 
                    _source.getGranularity().formatDate(responseDate));
            //Request
            String verb = request.getVerb();
            verb = verb.replaceFirst("Iterate", "List");
            writeRequest(request, verb, handler);
            handler.writeStartElementNS(NAMESPACE_URI, verb);
            try {
                for(OAIRecord record : iterable) {
                    count++;
                    write(record, handler);
                }
            }
            finally { iterable.close(); }

            handler.writeEndElement(); //verb
            handler.writeEndElement(); //OAI
            handler.writeEndDocument();
            return count;
        }
        catch(TransformerConfigurationException | SAXException e)
        {
            throw new IOException(e);
        }
        finally { iterable.close(); }
    }
}
