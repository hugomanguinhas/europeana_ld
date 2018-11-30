/**
 * 
 */
package eu.europeana.ld.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 28 Nov 2016
 */
public class RewriteSupport
{

    public Map<String,String> getRewrites(String url)
           throws IOException
    {
        return getRewrites((Map)JsonUtils.fromURL(new URL(url)));
    }

    public Map<String,String> getRewrites(InputStream is)
           throws IOException
    {
        try {
            return getRewrites((Map)JsonUtils.fromInputStream(is, "UTF-8"));
        }
        finally { IOUtils.closeQuietly(is); }
    }

    public Map<String,String> getRewrites(Map json)
    {
        Map<String,Object> ctxt = (Map<String,Object>)json.get("@context");
        if ( ctxt == null ) { return Collections.EMPTY_MAP; }

        Map<String,String> map = new HashMap();
        for ( String key : ctxt.keySet() )
        {
            String value = getValue(ctxt.get(key), map);
            if ( value != null ) { map.put(key, value); }
        }
        return map;
    }

    public Map<String,String> getUncompactRewrites(String url)
           throws IOException
    {
        return getUncompactRewrites(getRewrites(url));
    }

    public Map<String,String> getUncompactRewrites(Map<String,String> rewrites)
    {
        Map<String,String> map = new HashMap();
        for ( String key : rewrites.keySet() )
        {
            String value = rewrites.get(key);
            String old   = map.get(value);
            map.put(value
                  , old == null || old.length() > key.length() ? key : old);
        }
        return map;
    }

    private String getValue(Object value, Map<String,String> rewrites)
    {
        if ( value instanceof String ) { return (String)value; }

        if ( value instanceof Map )
        {
            Map    map = (Map)value;
            Object obj = map.get("@id");
            if ( obj == null || !(obj instanceof String) ) { return null; }

            return getQName((String)obj, rewrites);
        }

        return null;
    }

    private String getQName(String value, Map<String,String> rewrites)
    {
        int i = value.indexOf(':');
        if ( i <= 0 ) { return value; }
        String base = rewrites.get(value.substring(0, i));
        return ( base == null ? value : base + value.substring(i+1) );
    }
}
