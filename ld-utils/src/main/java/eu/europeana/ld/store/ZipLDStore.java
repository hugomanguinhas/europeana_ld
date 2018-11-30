/**
 * 
 */
package eu.europeana.ld.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.io.FileNaming;
import eu.europeana.ld.jena.DefaultWriter;
import eu.europeana.ld.jena.JenaUtils;
import eu.europeana.ld.jena.RecordWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 27 May 2016
 */
public class ZipLDStore implements LDStore
{
    private File            _src;
    private RecordWriter     _writer;
    private Lang            _format;
    private FileNaming      _naming;
    private File            _tmp;
    private ZipOutputStream _zos;
    private Set<String>     _entries = new TreeSet();

    public ZipLDStore(File src, RecordWriter writer
                    , Lang format, FileNaming naming)
    {
        _src    = src;
        _writer = writer;
        _format = format;
        _naming = naming;
    }

    public ZipLDStore(File src, Lang format)
    {
        this(src, new DefaultWriter(format), format
           , new FileNaming.DefaultFileNaming());
    }

    @Override
    public synchronized void begin()
    {
        try { _tmp = File.createTempFile("tmp_", ".zip"); }
        catch (IOException e) { e.printStackTrace(); return; }

        System.out.println("Creating temporary file: " + _tmp.getAbsolutePath());
        transfer(_src, _tmp);
    }

    @Override
    public synchronized boolean contains(String uri)
    {
        return _entries.contains(_naming.convert(uri, _format));
    }

    public synchronized void store(String uri, Model content)
    {
        String name = _naming.convert(uri, _format);
        if ( _entries.contains(name) ) { return; }

        _entries.add(name);
        try {
            _zos.putNextEntry(new ZipEntry(name));
            _writer.write(content.getResource(uri), _zos);
            _zos.closeEntry();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public synchronized void store(String uri, String content)
    {
        store(uri, new StringBufferInputStream(content));
    }

    @Override
    public synchronized void store(String uri, InputStream content)
    {
        String name = _naming.convert(uri, _format);
        if ( _entries.contains(name) ) { return; }

        _entries.add(name);
        try {
            _zos.putNextEntry(new ZipEntry(name));
            try { IOUtils.copy(content, _zos); } finally { _zos.closeEntry(); }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public synchronized void commit()
    {
        IOUtils.closeQuietly(_zos);
        _src.delete();
        _tmp.renameTo(_src);

        _zos = null;
        _entries.clear();
        _tmp = null;
    }

    private void transfer(File src, File dst)
    {
        ZipInputStream  zis = null;
        try {
            _zos = new ZipOutputStream(new FileOutputStream(dst));
            if ( !src.exists() ) { return; }

            zis  = new ZipInputStream(new FileInputStream(_src));
            ZipEntry entry = zis.getNextEntry();
            while ( entry != null )
            {
                String name = entry.getName();
                _entries.add(name);

                _zos.putNextEntry(new ZipEntry(name));
                IOUtils.copy(zis, _zos);
                _zos.closeEntry();

                entry = zis.getNextEntry();
            }
        }
        catch(IOException e) { e.printStackTrace(); }
        finally { IOUtils.closeQuietly(zis); }
    }
}
