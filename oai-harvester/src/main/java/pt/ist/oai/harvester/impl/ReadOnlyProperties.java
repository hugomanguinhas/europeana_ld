package pt.ist.oai.harvester.impl;

import java.util.*;

public class ReadOnlyProperties extends Properties
{
    private static final long serialVersionUID = 1L;

    public ReadOnlyProperties append(String param, String value)
    {
        setProperty(param, value);
        return this;
    }

    public ReadOnlyProperties appendAll(Properties props)
    {
        putAll(props);
        return this;
    }
}
