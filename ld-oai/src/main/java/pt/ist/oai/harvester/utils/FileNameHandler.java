/**
 * 
 */
package pt.ist.oai.harvester.utils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 26 Apr 2016
 */
public interface FileNameHandler
{

    public String getFilename(String id);


    public static class DefaultFileNameHandler implements FileNameHandler
    {
        @Override
        public String getFilename(String id)
        {
            return id.replaceAll("[<>:\"/\\|?*]", "_")+ ".xml";
        }
    }
}
