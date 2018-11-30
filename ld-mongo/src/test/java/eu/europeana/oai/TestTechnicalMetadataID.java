/**
 * 
 */
package eu.europeana.oai;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 30 May 2018
 */

import static eu.europeana.ld.mongo.TechnicalMetadataUtils.*;

public class TestTechnicalMetadataID
{

    public static final void main(String[] args)
    {
        System.out.println(getTechMetaID("/91955/u__fulletsblUB_217","http://mdc.cbuc.cat/utils/getfile/collection/fulletsblUB/id/217/filename/217.pdf"));

        System.out.println(getTechMetaID("/000002/_UEDIN_214", "http://dams.llgc.org.uk/iiif/image/2.0/1294670/full/512,/0/default.jpg"));
        System.out.println(getTechMetaID("/000002/_UEDIN_214", "http://www.mimo-db.eu/media/UEDIN/AUDIO/0032195s.mp3"));
        System.out.println(getTechMetaID("/000002/_UEDIN_214", "http://www.mimo-db.eu/media/UEDIN/IMAGE/0032195c.jpg"));
        System.out.println(getTechMetaID("/9200506/https___handrit_is_rdf_cho_JS04_0259", "http://myndir.handrit.is/file/JS_259_4to/JS_259_4to,_0001r_-_4.jpg"));
        
    }
}
