/**
 * 
 */
package eu.europeana.ld.tools;

import static eu.europeana.ld.tools.ToolkitUtils.getRDFFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.jena.riot.Lang;

import eu.europeana.ld.jena.DefaultWriter;
import eu.europeana.ld.jena.RecordWriter;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 9 Dec 2016
 */
public class ToolkitUtils
{

    public static Lang[] getRDFFormats(CommandLine line)
    {
        return getRDFFormats(line.getOptionValues("formats"));
    }

    public static Lang[] getRDFFormats(String[] strs)
    {
        Lang[] formats = new Lang[strs.length];
        for ( int i = 0; i < strs.length; i++ )
        {
            formats[i] = getRDFFormat(strs[i]);
        }
        return formats;
    }

    public static Lang getRDFFormat(CommandLine line)
    {
        String format = line.getOptionValue("format");
        return ( format == null ? null : getRDFFormat(format) );
    }

    public static Lang getRDFFormat(String ext)
    {
        if ( ext == null ) { return null; }

        if ( ext.equals("ttl")    ) { return Lang.TURTLE; }
        if ( ext.equals("xml")
          || ext.equals("rdf")    ) { return Lang.RDFXML; }
        if ( ext.equals("n3")     ) { return Lang.N3;     }
        if ( ext.equals("nt")     ) { return Lang.NT;     }
        if ( ext.equals("jsonld") ) { return Lang.JSONLD; }
        return null;
    }

    public static Lang getRDFFormatFromFilename(String fn)
    {
        int l = fn.length()-3;
        int i = fn.lastIndexOf('.', l-1);
        return getRDFFormat(fn.substring(i+1, l));
    }
}
