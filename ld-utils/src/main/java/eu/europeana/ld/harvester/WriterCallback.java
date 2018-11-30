/**
 * 
 */
package eu.europeana.ld.harvester;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Resource;

import eu.europeana.ld.jena.IterativeRecordWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Nov 2016
 */
public class WriterCallback implements LDHarvesterCallback
{
    private OutputStream          _out;
    private IterativeRecordWriter _writer;

    public WriterCallback(OutputStream out, IterativeRecordWriter writer)
    {
        _out    = out;
        _writer = writer;
    }


    /***************************************************************************
     * Interface LifecycleHarvesterCallback
     **************************************************************************/
    @Override
    public void handle(String uri, Resource r, Status status)
    {
        try                  { _writer.write(r);              }
        catch(IOException e) { throw new RuntimeException(e); }
        finally              { r.getModel().removeAll();      }
    }

    @Override
    public void begin()
    {
        try                  { _writer.init(_out);            } 
        catch(IOException e) { throw new RuntimeException(e); }
    }

    @Override
    public void finish()
    {
        try                  { _writer.close();               } 
        catch(IOException e) { throw new RuntimeException(e); }
    }
}