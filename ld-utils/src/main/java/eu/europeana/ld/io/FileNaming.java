/**
 * 
 */
package eu.europeana.ld.io;

import java.net.URLEncoder;

import org.apache.jena.riot.Lang;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 4 Nov 2016
 */
public interface FileNaming
{
    public String convert(String uri, Lang format);


    public static class DefaultFileNaming implements FileNaming
    {
        public String convert(String uri, Lang format)
        {
            int i = uri.indexOf("://");
            return appendExtension( i >= 0 ? uri.substring(i+3) : uri, format);
        }

        protected String appendExtension(String str, Lang format)
        {
            String ext = format.getFileExtensions().get(0);
            return str + "." + ext;
        }
    }

    public static class URLEncodeFileNaming implements FileNaming
    {
        public String convert(String uri, Lang format)
        {
            String ext = format.getFileExtensions().get(0);
            return URLEncoder.encode(uri) + "." + ext;
        }
    }
}
