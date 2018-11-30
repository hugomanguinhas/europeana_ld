/**
 * 
 */
package eu.europeana.ld.mongo;

import static eu.europeana.ld.mongo.MongoEDMConstants.ABOUT;

import org.bson.Document;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 30 May 2018
 */
public class TechnicalMetadataUtils
{
    private static HashFunction _hf = Hashing.md5();

    public static String getTechMetaID(String recordID, String url)
    {
        HashCode hashCode = _hf.newHasher()
                .putString(url, Charsets.UTF_8)
                .putString("-", Charsets.UTF_8)
                .putString(recordID, Charsets.UTF_8)
                .hash();
        return hashCode.toString();
    }
}
