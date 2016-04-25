package eu.europeana.ld.analysis.property;

import java.util.Locale;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;

public class LangFunctionalPropertyStat extends DefaultPropertyStat 
{
    public LangFunctionalPropertyStat(Property p) { this(p, false); }

    public LangFunctionalPropertyStat(Property p, boolean inv) { super(p); }

    @Override
    protected void newValue(Object node)
    {
        if (!(node instanceof Literal)) { super.newValue("URI"); return; }

        String str = ((Literal)node).getString();
        if (str.trim().equals("")) { super.newValue("EMPTY"); return; }

        Locale locale = Locale.forLanguageTag(str);
        if (!locale.getLanguage().equals("")) { super.newValue(locale); return;}

        super.newValue("OTHER");
    }
}
