package eu.europeana.ld.edm.owl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.util.FileUtils;

import static eu.europeana.ld.jena.JenaUtils.*;

public class OWLFormatsBuilder
{
    private static Lang[] FORMATS = { Lang.TTL, Lang.NT, Lang.N3 };

    private Collection<Lang> _formats = null;

    public OWLFormatsBuilder() { this(FORMATS); }

    public OWLFormatsBuilder(Lang... formats)
    {
        _formats = Arrays.asList(formats);
    }

    public void generateFormats(File file) throws IOException
    {
        Model m = load(file);

        for ( Lang format : _formats )
        {
            File dest = getFormatFile(file, format);
            System.err.println(dest.getAbsolutePath());
            store(m, dest);
        }
    }

    private File getFormatFile(File file, Lang lang)
    {
        String text = lang.getFileExtensions().get(0);
        String name = file.getName();
        String sext = FileUtils.getFilenameExt(name);
        name = name.substring(0, name.length() - sext.length()) + text;
        return new File(file.getParentFile(), name);
    }
}
