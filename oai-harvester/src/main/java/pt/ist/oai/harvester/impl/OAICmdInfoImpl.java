package pt.ist.oai.harvester.impl;

import java.util.Date;

import pt.ist.oai.harvester.model.*;

public class OAICmdInfoImpl implements OAICmdInfo
{
    protected long _cursor;
    protected long _completeListSize;
    protected Date _expirationDate;

    public OAICmdInfoImpl()
    {
        _cursor = 0;
        _completeListSize = -1;
        _expirationDate = null;
    }

    @Override
    public long    getCursor()           { return _cursor; }

    @Override
    public boolean hasCompleteListSize() { return (_completeListSize >= 0); }

    @Override
    public long    getCompleteListSize() { return _completeListSize; }

    @Override
    public boolean hasExpirationDate()   { return (_expirationDate != null); }

    @Override
    public Date    getExpirationDate()   { return _expirationDate; }
}
