/**
 * 
 */
package eu.europeana.ld.edm.io;

import static org.apache.jena.riot.Lang.*;

import org.apache.jena.riot.Lang;
import eu.europeana.ld.io.FileNaming;
import eu.europeana.ld.jena.DefaultWriter;
import eu.europeana.ld.jena.IterativeDefaultWriter;
import eu.europeana.ld.jena.IterativeRecordWriter;
import eu.europeana.ld.jena.RecordWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Dec 2016
 */
public class EDMIOUtils
{
    public static final int KB          = 1024;
    public static       int BUFFER_SIZE = 32 * KB;

    public static RecordWriter getWriter(Lang lang)
    {
        if ( lang == RDFXML ) { return new XMLRecordWriter();    }
        if ( lang == TURTLE ) { return new TurtleRecordWriter(); }
        return new DefaultWriter(lang);
    }

    public static IterativeRecordWriter getIterativeWriter(Lang lang
                                                         , boolean close)
    {
        if ( lang == RDFXML ) { return new XMLIterativeResourceWriter(close); }
        if ( lang == TURTLE ) { return new TurtleRecordWriter(close);         }
        return new IterativeDefaultWriter(lang);
    }

    public static FileNaming getDatasetFileNaming()
    {
        return new EDMDatasetFileNaming();
    }

    public static FileNaming getLocalIdFileNaming()
    {
        return new EDMLocalIdFileNaming();
    }
}
