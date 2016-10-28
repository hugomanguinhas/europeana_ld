/**
 * 
 */
package eu.europeana.ld.iri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.atlas.lib.Chars;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIComponents;
import org.apache.jena.iri.IRIFactory;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 28 Jun 2016
 */
public class IRINormalizer
{
    private static final Pattern p
        = Pattern.compile("(([^:/?#]*):)?" + // scheme
            "(//((([^/?#@]*)@)?" + // user
            "(\\[[^/?#]*\\]|([^/?#:]*))?" + // host
            "(:([^/?#]*))?))?" + // port
            "([^#?]*)?" + // path
            "(\\?([^#]*))?" + // query
            "(#(.*))?", // frag
            Pattern.DOTALL);

    private static final Pattern Encoding
        = Pattern.compile("(%[0-9a-f]{2})+");

    private StringBuffer _sb     = new StringBuffer(5000);
    private StringBuffer _sb_iri = new StringBuffer(5000);

    public static String fixEscapes(String uri)
    {
        StringBuffer decoded = new StringBuffer();
        Matcher matcher = Encoding.matcher(uri);
        while (matcher.find())
        {
            matcher.appendReplacement(decoded, matcher.group().toUpperCase());
        }
        matcher.appendTail(decoded);
        return decoded.toString();
    }

    public String normalize(String iri)
    {
        String newIRI = iri;
        while ( true )
        {
            try { return normalizeImpl(newIRI); }
            catch (ReStartException e) { newIRI = e.getIRI(); }
        }
    }

    private String normalizeImpl(String iri)
    {
        iri = iri.trim();
        Matcher m = p.matcher(iri);
        if (!m.matches()) { throw new RuntimeException("not meant to happen"); }

        //IRIFactory factory = IRIFactory.jenaImplementation();
        //IRI iriObj = factory.construct(scheme, authority, path, query, fragment);
        //return iriObj.toString();

        _sb_iri.setLength(0);
        String scheme = processScheme(m.group(IRIComponents.SCHEME));
        if (scheme != null ) { _sb_iri.append(scheme).append(':'); }

        String authority = m.group(IRIComponents.AUTHORITY);
        if (authority != null ) {
            _sb_iri.append("//");
            String user = m.group(IRIComponents.USER);
            if (user != null ) { _sb_iri.append(user).append('@'); }

            String host = processHost(m.group(IRIComponents.HOST), iri);
            _sb_iri.append(host);

            String port = processPort(m.group(IRIComponents.PORT));
            if ( port != null ) { _sb_iri.append(':').append(port); }
        }

        String path  = processPath(m.group(IRIComponents.PATH));
        _sb_iri.append(path);

        String query = processQuery(m.group(IRIComponents.QUERY));
        if ( query != null ) { _sb_iri.append('?').append(query); }

        String fragment = processFragment(m.group(IRIComponents.FRAGMENT));
        if ( fragment != null ) { _sb_iri.append('#').append(fragment); }

        return _sb_iri.toString();
    }


    /***************************************************************************
     * Part Processors
     **************************************************************************/

    protected String processPort(String str)
    {
        if ( str == null ) { return null; }

        str = str.trim();
        return ( str.isEmpty() ? null : str);
    }

    protected String processHost(String str, String iri)
    {
        if ( str == null ) { return null; }

        int i = str.indexOf(' ');
        if ( i < 0 ) { return str; }

        String host = str.substring(0, i) + "/" + str.substring(i+1).trim();
        throw new ReStartException(iri.replace(str, host));
    }

    protected String processScheme(String str)
    {
        if ( str == null ) { return null; }
        
        return str;
    }

    protected String processAuthority(String str)
    {
        return str;
    }

    protected String processPath(String str)
    {
        if ( str == null ) { return null; }
        return processCharacters(str, new PathChecker());
    }

    protected String processQuery(String str)
    {
        if ( str == null ) { return null; }
        return processCharacters(str, new QueryChecker());
    }

    protected String processFragment(String str)
    {
        if ( str == null ) { return null; }
        return processCharacters(str, new FragmentChecker());
    }

    protected String processCharacters(String str, CharacterChecker checker)
    {
        _sb.setLength(0);
        int iL = str.length();
        for ( int i = 0; i < iL; )
        {
            char c = str.charAt(i++);

            if ( c == '%') {
                if ( i + 2 > iL ) { escape(c); continue; }

                char c1 = str.charAt(i);
                char c2 = str.charAt(i+1);
                if ( !isHex(c1) || !isHex(c2) ) { escape(c); continue; }

                _sb.append(c).append(Character.toUpperCase(c1))
                             .append(Character.toUpperCase(c2));
                i = i + 2;
                continue;
            }

            if ( checker.check(c) ) { _sb.append(c); continue; }

            escape(c);
        }
        return _sb.toString();
    }

    private void escape(char c)
    {
        _sb.append('%').append(Integer.toHexString(c).toUpperCase());
    }


    /***************************************************************************
     * Character Checks
     **************************************************************************/

    private boolean isFragment(char c)
    {
        return (isPChar(c) || c == '/' || c == '?');
    }

    private boolean isQuery(char c)
    {
        return (isPChar(c) || isPrivate(c) ||  c == '/' ||  c == '?');
    }

    private boolean isPath(char c)
    {
        return (isPChar(c) || c == '/');
    }

    private boolean isPrivate(char c)
    {
        return ( c >= 0xE000    && c <= 0xF8FF   )
            || ( c >= 0xF0000   && c <= 0xFFFFD  )
            || ( c >= 0xF100000 && c <= 0x10FFFD );
    }

    private boolean isPChar(char c)
    {
        return (isUnReserved(c) || isSubDelims(c) || c == ':' || c == '@');
    }

    private boolean isSubDelims(char c)
    {
        return ( c == '!' || c == '$' || c == '&' || c == '\'' || c == '('
              || c == ')' || c == '*' || c == '+' || c == ','
              || c == ';' || c == '=');
    }

    private boolean isUnReserved(char c)
    {
        return ( isAlpha(c) || isDigit(c) || isUCSChar(c) 
              || c == '-' || c == '.' || c == '_' || c == '~' );
    }

    private boolean isUCSChar(char c)
    {
        return ( c >= 0xA0    && c <= 0xD7FF  )
            || ( c >= 0xF900  && c <= 0xFDCF  )
            || ( c >= 0xFDF0  && c <= 0xFFEF  )
            || ( c >= 0x10000 && c <= 0x1FFFD )
            || ( c >= 0x20000 && c <= 0x2FFFD )
            || ( c >= 0x30000 && c <= 0x3FFFD )
            || ( c >= 0x40000 && c <= 0x4FFFD )
            || ( c >= 0x50000 && c <= 0x5FFFD )
            || ( c >= 0x60000 && c <= 0x6FFFD )
            || ( c >= 0x70000 && c <= 0x7FFFD )
            || ( c >= 0x80000 && c <= 0x8FFFD )
            || ( c >= 0x90000 && c <= 0x9FFFD )
            || ( c >= 0xA0000 && c <= 0xAFFFD )
            || ( c >= 0xB0000 && c <= 0xBFFFD )
            || ( c >= 0xC0000 && c <= 0xCFFFD )
            || ( c >= 0xD0000 && c <= 0xDFFFD )
            || ( c >= 0xE1000 && c <= 0xEFFFD )
            ;
    }

    private boolean isAlpha(char c)
    {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z' ));
    }

    private boolean isDigit(char c)
    {
        return (c >= '0' && c <= '9');
    }

    private boolean isHex(char c)
    {
        return ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F' )
             || (c >= '0' && c <= '9'));
    }

    private interface CharacterChecker
    {
        public boolean check(char c);
    }

    private class FragmentChecker implements CharacterChecker
    {
        public boolean check(char c) { return isFragment(c); }
    }

    private class QueryChecker implements CharacterChecker
    {
        public boolean check(char c) { return isQuery(c); }
    }

    private class PathChecker implements CharacterChecker
    {
        public boolean check(char c) { return isPath(c); }
    }

    private class ReStartException extends RuntimeException
    {
        private String _iri;

        public ReStartException(String iri) { _iri = iri; }

        public String getIRI() { return _iri; }
    }

    public static final void main(String[] args)
    {
        String[] strs = new String[] {
            "http://urn:nbn:se:kau:diva-4985"
          , "http://resolver.kb.nl/resolve?urn=urn:gvn:NOM01:fi-0120&role=thumbnail%20"
          , "http://mediateca.uniovi.es   Universidad de Oviedo (Creative Commons: BY NC ND)"
          , "mms://151.1.148.222/WMedia/300/MpegD300/D014402.wmv"
          , "https://rosa.roskildebib.dk/CalmView/Record.aspx?src=CalmView.Catalog&id=DS%2f1%2f78.06%2f5769"
          , "http://unknown-base/#Herakles#%20(Relief)%20-%20Kulturkreis:%20griechisch"
        };
        
        IRINormalizer n = new IRINormalizer();
        for ( String s : strs )
        {
            System.out.println(n.normalize(s));
        }
        //Chars.encodeAsHex(buff, marker, ch) ;
        //String hexstr = Integer.toHexString(c).toUpperCase();
        //System.out.println(n.normalize("http://unknown-base/#Herakles#%20(Relief)%20-%20Kulturkreis:%20griechisch"));
        //IRIFactory.iriImplementation().construct("");
    }
}
