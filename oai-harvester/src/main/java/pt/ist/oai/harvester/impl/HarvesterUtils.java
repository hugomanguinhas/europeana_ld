package pt.ist.oai.harvester.impl;

import static pt.ist.oai.harvester.OAIConstants.*;

import java.net.URLEncoder;
import java.text.*;
import java.util.*;

import org.xml.sax.*;

import pt.ist.oai.harvester.model.OAIDataSource.*;
import pt.ist.xml.parser.*;

public class HarvesterUtils
{
    public static List<String> getContentAsList(ParsedObject obj
                                              , String localname)
    {
        List<String> list = new Vector<String>();
        for(ParsedObject cobj : obj.getChildrenNS(NAMESPACE_URI, localname))
        {
            list.add(cobj.getText());
        }
        return list;
    }

    public static String getContent(ParsedObject obj, String localname)
    {
        ParsedObject cobj = obj.getChildNS(NAMESPACE_URI, localname);
        return (cobj != null ? cobj.getText() : null );
    }

    public static Date getNormalizedDate(String date
                                       , GranularityType granularity)
           throws SAXException
    {
        if(date == null) { return null; }

        try { return granularity.parseDate(date); }
        catch(ParseException e) { return null; }
    }

    public static String asNormalizedDate(Date date, GranularityType gt)
    {
        return gt.formatDate(date);
    }

    public static Long asLong(String text)
    {
        return ( text == null ? null : new Long(text) );
    }

    public static <E extends Enum<?>> E parseEnum(Class<E> c, String text)
    {
        if(text == null) { return null; }

        for(E e : c.getEnumConstants())
        {
            if(e.toString().equalsIgnoreCase(text)) { return e; }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static String append(String url, String param, String value)
    {
        if(value == null) { return url; }
        else { return url + "&" + param + "=" + URLEncoder.encode(value); }
    }

    @SuppressWarnings("deprecation")
    public static String append(String url, Properties props)
    {
        if(props == null) { return url; }

        StringBuilder sb = new StringBuilder(url);
        for(Map.Entry<Object, Object> entry : props.entrySet())
        {
            String value = entry.getValue().toString();
            sb.append("&" + entry.getKey() + "=" + URLEncoder.encode(value));
        }
        return sb.toString();
    }
}
