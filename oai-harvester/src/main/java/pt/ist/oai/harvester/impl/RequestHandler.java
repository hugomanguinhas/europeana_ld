package pt.ist.oai.harvester.impl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.apache.log4j.*;

import pt.ist.oai.harvester.*;

public class RequestHandler
{
    protected static int MAX_RETRIES = 10;

    private static Logger log = Logger.getLogger(OAIHarvester.class);

    public static InputStream handle(String requestURL) throws IOException
    {
        URL url = new URL(requestURL);
        HttpURLConnection con = null;
        int responseCode = 0;
        int retries = MAX_RETRIES;
        do {
            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("User-Agent", "OAIHarvester/2.0");
            con.setRequestProperty("Accept-Encoding", "compress, gzip, identify");
            try {
                responseCode = con.getResponseCode();
            }
            catch(FileNotFoundException e) {
                // assume it's a 503 response
                log.info(requestURL, e);
                responseCode = HttpURLConnection.HTTP_UNAVAILABLE;
            }

            if(responseCode != HttpURLConnection.HTTP_OK) {

                if(retries <= 0) break;
                retries--;

                long retrySeconds = con.getHeaderFieldInt("Retry-After", -1);
                if (retrySeconds < 0) {
                    long now = (new Date()).getTime();
                    long retryDate = con.getHeaderFieldDate("Retry-After", now);
                    if(retryDate > 0) retrySeconds = retryDate - now;
                    else retrySeconds = 10;
                }
                /*
                if(retrySeconds == 0) { // Apparently, it's a bad URL
                    throw new FileNotFoundException("Bad URL?");
                }
                */
                log.info("Server response: Retry-After=" + retrySeconds);
                if(retrySeconds >= 0) {
                    try {
                        Thread.sleep(retrySeconds * 1000);
                    }
                    catch(InterruptedException ex) {
                    }
                }
            }
        }
        while(responseCode != HttpURLConnection.HTTP_OK);

        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException(
                "Server returned HTTP response code: " + responseCode + 
                " for URL: " + requestURL);
        }

        String contentEncoding = con.getHeaderField("Content-Encoding");

        if("compress".equals(contentEncoding))
        {
            ZipInputStream zis = new ZipInputStream(con.getInputStream());
            zis.getNextEntry();
            return zis;
        }
        else if ("gzip".equals(contentEncoding)) {
            return new GZIPInputStream(con.getInputStream());
        }
        else if ("deflate".equals(contentEncoding)) {
            return new InflaterInputStream(con.getInputStream());
        }
        else {
            return con.getInputStream();
        }
    }
}
