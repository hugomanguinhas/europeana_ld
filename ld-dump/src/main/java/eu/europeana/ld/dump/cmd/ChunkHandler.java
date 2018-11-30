/**
 * 
 */
package eu.europeana.ld.dump.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.ResourceCallback;
import eu.europeana.ld.harvester.LDHarvesterCallback;
import eu.europeana.ld.jena.IterativeRecordWriter;
import static org.apache.commons.lang3.StringUtils.*;
import static eu.europeana.ld.edm.io.EDMIOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Dec 2016
 */
public abstract class ChunkHandler implements LDHarvesterCallback
{
    public static int DEFAULT_CHUNK_SIZE = 10000;

    public static class DirChunkHandler extends ChunkHandler
    {
        private FileOutputStream _fos;

        public DirChunkHandler(File output, Lang format, int chunkSize)
        {
            super(output, format, chunkSize);
        }

        @Override
        public void begin()
        {
            _writer = getIterativeWriter(_format, true);
        }

        @Override
        protected void startChunk(Status s) throws IOException
        {
            String fn   = getFileName(_format, s);
            File   file = new File(_output, fn);
            if ( !_output.exists() ) { _output.mkdirs(); }

            _fos = new FileOutputStream(file);
            _writer.init(_fos);
        }

        @Override
        protected void endChunk() throws IOException
        {
            if ( _fos == null ) { return; }
            _writer.close();
        }
    }

    public static class ZipChunkHandler extends ChunkHandler
    {
        private static Charset UTF8 = Charset.forName("UTF-8");
        private ZipOutputStream _zos;

        public ZipChunkHandler(File output, Lang format, int chunkSize)
        {
            super(output, format, chunkSize);
        }

        @Override
        protected void startChunk(Status s) throws IOException
        {
            if ( _zos == null ) { 
                _zos = new ZipOutputStream(new FileOutputStream(_output), UTF8);
            }

            _zos.putNextEntry(new ZipEntry(getFileName(_format, s)));
            _writer.init(_zos);
        }

        protected void endChunk() throws IOException
        {
            if ( _zos == null ) { return; }
            _zos.closeEntry();
            _writer.close();
        }
    }

    protected File           _output;
    protected Lang           _format;
    protected int            _recordCount = 0;
    protected int            _chunkCount  = 0;
    protected int            _chunkSize   = 0;
    protected IterativeRecordWriter _writer;

    public ChunkHandler(File output, Lang format, int chunkSize)
    {
        _output    = output;
        _format    = format;
        _chunkSize = chunkSize;
    }

    @Override
    public void handle(Resource r, ResourceCallback.Status s)
    {
        try {
            checkChunk(s);

            _writer.write(r);

            _recordCount++;
        }
        catch(IOException e) { throw new RuntimeException(e); }
        finally              { r.getModel().removeAll();      }
    }

    @Override
    public void begin()
    {
        _writer = getIterativeWriter(_format, false);
    }

    @Override
    public void finish()
    {
        try                   { endChunk();                    }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    protected abstract void startChunk(Status s) throws IOException;

    protected abstract void endChunk() throws IOException;

    protected void checkChunk(Status s) throws IOException
    {
        if ( _recordCount > 0 && _recordCount < _chunkSize ) { return; }

        endChunk();

        _recordCount = 0;

        startChunk(s);

        _chunkCount++;
    }

    protected String getFileName(Lang lang, Status s)
    {
        int digits = String.valueOf(((int)s.total / _chunkSize) + 1).length();
        String ext = lang.getFileExtensions().get(0);
        String str = leftPad(String.valueOf(_chunkCount), digits, '0');
        return "dump_" + str + "." + ext;
    }
}
