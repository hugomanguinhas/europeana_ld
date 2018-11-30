/**
 * 
 */
package eu.europeana.ld.dump.cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.harvester.LDHarvesterCallback;
import eu.europeana.ld.harvester.WriterCallback;
import eu.europeana.ld.jena.ZipRecordWriter;
import static eu.europeana.ld.edm.EuropeanaDataUtils.*;
import static eu.europeana.ld.edm.io.EDMIOUtils.*;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Dec 2016
 */
public class DatasetHandler implements LDHarvesterCallback
{
    private File                 _dir;
    private String               _ds;
    private Lang[]               _formats;
    private List<WriterCallback> _writers = Collections.emptyList();

    public DatasetHandler(File dir, Lang[] formats)
    {
        _dir     = dir;
        _ds      = null;
        _formats = formats;
    }

    @Override
    public void handle(Resource r, Status s)
    {
        try {
            String[] id = getDatasetAndLocalID(r.getURI());
            if ( id == null ) { return; }
    
            String ds = id[0];
            if ( !ds.equals(_ds) ) { switchDataset(ds); }
            writeToWriters(r, s);
        }
        catch(IOException e) { throw new RuntimeException(e); }
    }

    @Override
    public void begin() {}

    @Override
    public void finish() {}

    protected void switchDataset(String ds) throws IOException
    {
        closeWriters();
        _ds = ds;
        _writers = getWriters(getDatasetsDir(), _formats);
        beginWriters();
    }

    protected void writeToWriters(Resource r, Status status)
    {
        for ( WriterCallback cb : _writers ) { cb.handle(r, status); }
    }

    protected void beginWriters()
    {
        for ( WriterCallback cb : _writers ) { cb.begin(); }
    }

    protected void closeWriters()
    {
        for ( WriterCallback cb : _writers ) { cb.finish(); }
    }

    protected File getDatasetsDir()
    {
        File file = new File(_dir, _ds);
        if ( !file.exists() ) { file.mkdirs(); }
        return file;
    }

    protected File getFile(File dir, Lang lang)
    {
        return new File(dir, "dump." + lang.getFileExtensions().get(0) + ".zip");
    }

    protected List<WriterCallback> getWriters(File dir, Lang[] formats)
              throws IOException
    {
        List<WriterCallback> list = new ArrayList(formats.length);
        for ( Lang format : formats )
        {
            File file = getFile(dir, format);
            list.add(getZipCallback(file, format));
        }
        return list;
    }

    protected WriterCallback getZipCallback(File file, Lang lang)
              throws IOException
    {
        FileOutputStream fos    = new FileOutputStream(file);
        ZipRecordWriter  writer = new ZipRecordWriter(getWriter(lang), lang
                                                     , getLocalIdFileNaming()
                                                     , true);
        return new WriterCallback(fos, writer);
    }
}
