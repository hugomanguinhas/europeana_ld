/**
 * 
 */
package eu.europeana.ld.jena;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.io.FileNaming;
import eu.europeana.ld.jena.DefaultWriter;
import eu.europeana.ld.jena.RecordWriter;
import static org.apache.commons.io.IOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 May 2016
 */
public class ZipRecordWriter implements IterativeRecordWriter
{
    private RecordWriter    _writer;
    private Lang            _format;
    private FileNaming      _naming;
    private ZipOutputStream _zos;
    private boolean         _closeNested;

    public ZipRecordWriter(RecordWriter writer, Lang format
                         , FileNaming naming, boolean close)
    {
        _writer      = writer;
        _format      = format;
        _naming      = naming;
        _closeNested = close;
    }

    public ZipRecordWriter(Lang format)
    {
        this(new DefaultWriter(format), format
                             , new FileNaming.DefaultFileNaming(), true);
    }

    /***************************************************************************
     * Interface IterativeRecordWriter
     **************************************************************************/

    @Override
    public synchronized void init(OutputStream out)
    {
        _zos = new ZipOutputStream(out, Charset.forName("UTF-8"));
    }

    @Override
    public synchronized void write(Resource r) throws IOException
    {
        String name = _naming.convert(r.getURI(), _format);
        _zos.putNextEntry(new ZipEntry(name));
        _writer.write(r, _zos);
        _zos.closeEntry();
    }


    /***************************************************************************
     * Interface Closeable
     **************************************************************************/

    @Override
    public synchronized void close()
    {
        if ( _closeNested ) { closeQuietly(_zos); }
        _zos = null;
    }
}
