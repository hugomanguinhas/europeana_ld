/**
 * 
 */
package pt.ist.oai.harvester.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;

import pt.ist.oai.harvester.OAIHarvester;
import pt.ist.oai.harvester.OAIHarvesterImpl;
import pt.ist.oai.harvester.model.OAICmdInfo;
import pt.ist.oai.harvester.model.OAIRecord;
import pt.ist.oai.harvester.model.OAIRecordHeader;
import pt.ist.oai.harvester.model.OAIRequest;
import pt.ist.util.iterator.CloseableIterable;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public class OAI2FileHarvester
{
    private OAIHarvester    _harvester;
    private FileNameHandler _handler;
    private Transformer     _transformer;


    public OAI2FileHarvester(String url)
    {
        this(new OAIHarvesterImpl(url));
    }

    public OAI2FileHarvester(String url, FileNameHandler handler)
    {
        this(new OAIHarvesterImpl(url), handler);
    }

    public OAI2FileHarvester(OAIHarvester harvester)
    {
        this(harvester, new FileNameHandler.DefaultFileNameHandler());
    }

    public OAI2FileHarvester(OAIHarvester harvester, FileNameHandler handler)
    {
        _harvester   = harvester;
        _handler     = handler;

        try { _transformer = TransformerFactory.newInstance().newTransformer(); }
        catch (TransformerConfigurationException
             | TransformerFactoryConfigurationError e) { e.printStackTrace(); }
    }

    public void harvest(String set, String format, File file)
    {
        if ( file == null ) { return; }

        if ( isZip(file) ) { harvest2zip(set, format, file); return; }

        file.mkdirs();
        harvest2dir(set, format, file);
    }

    protected void harvest2zip(String set, String format, File file)
    {
        CloseableIterable<OAIRecord> iter = null;
        ZipOutputStream out = null;
        try {
            out = getZipOutputStream(file);
            OAIRequest<CloseableIterable<OAIRecord>> req
                = _harvester.newIterateRecords(set, format);
            iter = req.handle();
            for(OAIRecord record : iter) { store(record, out); }

            OAICmdInfo info = req.getInfo();
            if ( info.hasResponseDate() ) {
                file.setLastModified(info.getResponseDate().getTime());
            }
        }
        catch (TransformerException | IOException e)
        {
            e.printStackTrace();
        }
        finally { IOUtils.closeQuietly(out); iter.close(); }
    }

    protected void harvest2dir(String set, String format, File file) {}

    protected ZipEntry getZipEntry(String id)
    {
        return new ZipEntry(_handler.getFilename(id));
    }

    protected boolean isZip(File file)
    {
        if ( file.getName().endsWith(".zip") ) { return true; }
        return false;
    }


    private ZipOutputStream getZipOutputStream(File file)
    {
        file.getParentFile().mkdirs();
        try { return new ZipOutputStream(new FileOutputStream(file)); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        return null;
    }

    private void store(OAIRecord record, ZipOutputStream out)
            throws TransformerException, IOException
    {
        OAIRecordHeader header = record.getHeader();
        String id = header.getIdentifier();
        //log.info("Processing record: " + id);
        try {
            ZipEntry entry = getZipEntry(id);
            entry.setTime(header.getDatestamp().getTime());
            entry.setComment(id);
            out.putNextEntry(entry);
            _transformer.transform(new DOMSource(record.getMetadata())
                                 , new StreamResult(out));
            out.flush();
        }
        finally { out.closeEntry(); }
    }
}
