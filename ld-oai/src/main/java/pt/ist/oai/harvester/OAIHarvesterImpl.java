package pt.ist.oai.harvester;

import pt.ist.oai.harvester.exceptions.OAIException;
import pt.ist.oai.harvester.impl.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.oai.harvester.model.OAIDataSource.GranularityType;
import pt.ist.util.iterator.CloseableIterable;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.impl.client.HttpClientBuilder;

import static pt.ist.oai.harvester.impl.HarvesterUtils.asNormalizedDate;

public class OAIHarvesterImpl implements OAIHarvester
{
    protected OAIDataSource     _dataSource;
    protected HttpClientBuilder _builder;

    public OAIHarvesterImpl(String baseURL, HttpClientBuilder builder)
    {
        _dataSource = new Identify(builder).identify(baseURL);
        _dataSource.setBaseURL(baseURL);
        _builder = builder;
    }

    public OAIHarvesterImpl(String baseURL)
    {
        this(baseURL, HttpClientBuilder.create());
    }


    /***************************************************************************
     * Interface OAIHarvester
     **************************************************************************/
    //Identity
    @Override
    public OAIDataSource identify() throws OAIException { return _dataSource; }

    //List Metadata Formats
    @Override
    public List<OAIMetadataFormat> listMetadataFormats() throws OAIException
    {
        return newListMetadataFormats().handle();
    }

    @Override
    public List<OAIMetadataFormat> listMetadataFormats(String itemIdentifier)
           throws OAIException
    {
        return newListMetadataFormats(itemIdentifier).handle();
    }

    public List<OAIMetadataFormat> listMetadataFormats(
           Properties props) throws OAIException
    {
        return newListMetadataFormats(props).handle();
    }

    //List Identifiers
    @Override
    public List<OAIRecordHeader> listIdentifiers(
           String set) throws OAIException
    {
        return newListIdentifiers(set).handle();
    }

    @Override
    public List<OAIRecordHeader> listIdentifiers(
            String set, String metadataPrefix) throws OAIException
    {
        return newListIdentifiers(set, metadataPrefix).handle();
    }

    @Override
    public List<OAIRecordHeader> listIdentifiers(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException
    {
        return newListIdentifiers(set, metadataPrefix, from, to).handle();
    }

    @Override
    public List<OAIRecordHeader> listIdentifiers(
           Properties props) throws OAIException
    {
        return newListIdentifiers(props).handle();
    }

    @Override
    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           String set) throws OAIException
    {
        return newIterateIdentifiers(set).handle();
    }

    @Override
    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           String set, String metadataPrefix) throws OAIException
    {
        return newIterateIdentifiers(set, metadataPrefix).handle();
    }

    @Override
    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException
    {
        return newIterateIdentifiers(set, metadataPrefix, from, to).handle();
    }

    @Override
    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           Properties props) throws OAIException
    {
        return newIterateIdentifiers(props).handle();
    }

    //List Sets
    @Override
    public List<OAIMetadataSet> listSets() throws OAIException
    {
        return newListSets().handle();
    }

    @Override
    public CloseableIterable<OAIMetadataSet> iterateSets() throws OAIException
    {
        return newIterateSets().handle();
    }

    //Get Record
    @Override
    public OAIRecord getRecord(Properties props) throws OAIException
    {
        return newGetRecord(props).handle();
    }

    @Override
    public OAIRecord getRecord(String id, String metadataPrefix)
           throws OAIException
    {
        return newGetRecord(id, metadataPrefix).handle();
    }

    //List Records
    @Override
    public List<OAIRecord> listRecords(Properties props) throws OAIException
    {
        return newListRecords(props).handle();
    }

    @Override
    public List<OAIRecord> listRecords(String metadataPrefix) 
           throws OAIException
    {
        return newListRecords(metadataPrefix).handle();
    }

    @Override
    public List<OAIRecord> listRecords(
           String set, String metadataPrefix) throws OAIException
    {
        return newListRecords(set, metadataPrefix).handle();
    }

    @Override
    public List<OAIRecord> listRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException
    {
        return newListRecords(set, metadataPrefix, from, to).handle();
    }

    //Iterate Records
    @Override
    public CloseableIterable<OAIRecord> iterateRecords(
           Properties props) throws OAIException
    {
        return newIterateRecords(props).handle();
    }

    @Override
    public CloseableIterable<OAIRecord> iterateRecords(
           String metadataPrefix) throws OAIException
    {
        return newIterateRecords(metadataPrefix).handle();
    }

    @Override
    public CloseableIterable<OAIRecord> iterateRecords(
           String set, String metadataPrefix) throws OAIException
    {
        return newIterateRecords(set, metadataPrefix).handle();
    }

    @Override
    public CloseableIterable<OAIRecord> iterateRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException
    {
        return newIterateRecords(set, metadataPrefix, from, to).handle();
    }

    //Count Records
    @Override
    public long countRecords(Properties props) throws OAIException
    {
        return newCountRecords(props).handle();
    }

    @Override
    public long countRecords(String metadataPrefix) throws OAIException
    {
        return newCountRecords(metadataPrefix).handle();
    }

    @Override
    public long countRecords(String set, String metadataPrefix)
           throws OAIException
    {
        return newCountRecords(set, metadataPrefix).handle();
    }

    @Override
    public long countRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException
    {
        return newCountRecords(set, metadataPrefix, from, to).handle();
    }

    /***************************************************************************
     * Methods for building requests
     **************************************************************************/
    //List Metadata Formats
    public OAIRequest<List<OAIMetadataFormat>> newListMetadataFormats(
           Properties props)
    {
        return new ListMetadataFormats(_dataSource, props, _builder);
    }

    public OAIRequest<List<OAIMetadataFormat>> newListMetadataFormats()
    {
        return new ListMetadataFormats(_dataSource, createProp(), _builder);
    }

    public OAIRequest<List<OAIMetadataFormat>> newListMetadataFormats(
           String itemIdentifier)
    {
        return new ListMetadataFormats(_dataSource, createProp().
            append("identifier", itemIdentifier), _builder);
    }

    //List Identifiers
    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           Properties props)
    {
        return new ListIdentifiers(_dataSource, props, _builder);
    }

    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           String set)
    {
        return new ListIdentifiers(_dataSource, createProp().append("set",set)
                                 , _builder);
    }

    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           String set, String metadataPrefix)
    {
        return new ListIdentifiers(
                       _dataSource
                      , createProp().append("set"           , set)
                                    .append("metadataPrefix", metadataPrefix)
                      , _builder);
    }

    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           String set, String metadataPrefix, Date from, Date to)
    {
        GranularityType type = _dataSource.getGranularity();
        return new ListIdentifiers(
                       _dataSource
                     , createProp().append("set"           , set)
                                   .append("metadataPrefix", metadataPrefix)
                                   .append("from"          , asNormalizedDate(from, type))
                                   .append("until"         , asNormalizedDate(to, type))
                     , _builder);
    }


    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           Properties props)
    {
        return new IterateIdentifiers(_dataSource, props, _builder);
    }

    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           String set)
    {
        return new IterateIdentifiers(_dataSource, createProp()
            .append("set", set), _builder);
    }

    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           String set, String metadataPrefix)
    {
        return new IterateIdentifiers(_dataSource, createProp()
            .append("set"           , set)
            .append("metadataPrefix", metadataPrefix), _builder);
    }

    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           String set, String metadataPrefix, Date from, Date to)
    {
        GranularityType type = _dataSource.getGranularity();
        return new IterateIdentifiers(_dataSource, createProp()
            .append("set"           , set)
            .append("metadataPrefix", metadataPrefix)
            .append("from"          , asNormalizedDate(from, type))
            .append("until"         , asNormalizedDate(to, type)), _builder);
    }

    //List Sets
    public OAIRequest<List<OAIMetadataSet>> newListSets()
    {
        return new ListSets(_dataSource, createProp(), _builder);
    }

    public OAIRequest<CloseableIterable<OAIMetadataSet>> newIterateSets()
    {
        return new IterateSets(_dataSource, createProp(), _builder);
    }

    //Get Record
    public OAIRequest<OAIRecord> newGetRecord(Properties props)
    {
        return new GetRecord(_dataSource, props, _builder);
    }

    public OAIRequest<OAIRecord> newGetRecord(String id, String metadataPrefix)
    {
        return new GetRecord(
                       _dataSource
                     , createProp().append("identifier", id)
                                   .append("metadataPrefix", metadataPrefix)
                     , _builder);
    }

    //List Records
    public OAIRequest<List<OAIRecord>> newListRecords(Properties props)
    {
        return new ListRecords(_dataSource, props, _builder);
    }

    public OAIRequest<List<OAIRecord>> newListRecords(
           String metadataPrefix)
    {
        return new ListRecords(_dataSource, createProp()
            .append("metadataPrefix", metadataPrefix), _builder);
    }

    public OAIRequest<List<OAIRecord>> newListRecords(
           String set, String metadataPrefix)
    {
        return new ListRecords(_dataSource, createProp()
            .append("set"           , set)
            .append("metadataPrefix", metadataPrefix), _builder);
    }

    public OAIRequest<List<OAIRecord>> newListRecords(
           String set, String metadataPrefix, Date from, Date to)
    {
        GranularityType type = _dataSource.getGranularity();
        return new ListRecords(_dataSource, createProp()
            .append("set"           , set)
            .append("metadataPrefix", metadataPrefix)
            .append("from"          , asNormalizedDate(from, type))
            .append("until"         , asNormalizedDate(to, type)), _builder);
    }

    //Iterate Records
    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           Properties props)
    {
        return new IterateRecords(_dataSource, props, _builder);
    }

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           String metadataPrefix)
    {
        return new IterateRecords(_dataSource, createProp()
            .append("metadataPrefix", metadataPrefix), _builder);
    }

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           String set, String metadataPrefix)
    {
        return new IterateRecords(_dataSource, createProp()
            .append("set"           , set)
            .append("metadataPrefix", metadataPrefix), _builder);
    }

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           String set, String metadataPrefix, Date from, Date to)
    {
        GranularityType type = _dataSource.getGranularity();
        return new IterateRecords(_dataSource, createProp()
            .append("set"           , set)
            .append("metadataPrefix", metadataPrefix)
            .append("from"          , asNormalizedDate(from, type))
            .append("until"         , asNormalizedDate(to, type)), _builder);
    }

    //Count Records
    @Override
    public OAIRequest<Long> newCountRecords(
           Properties props) throws OAIException
    {
        return new CountRecords(_dataSource, props, _builder);
    }

    @Override
    public OAIRequest<Long> newCountRecords(
           String metadataPrefix) throws OAIException
    {
        return new CountRecords(_dataSource, createProp().
            append("metadataPrefix", metadataPrefix), _builder);
    }

    @Override
    public OAIRequest<Long> newCountRecords(
           String set, String metadataPrefix) throws OAIException
    {
        return new CountRecords(
                       _dataSource
                     , createProp().append("set", set)
                                   .append("metadataPrefix", metadataPrefix)
                     , _builder);
    }

    @Override
    public OAIRequest<Long> newCountRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException
    {
        return new CountRecords(_dataSource, createProp().
            append("set", set).append("metadataPrefix", metadataPrefix).
            append("from", asNormalizedDate(from, _dataSource.getGranularity())).
            append("until", asNormalizedDate(to, _dataSource.getGranularity()))
            , _builder);
    }


    /***************************************************************************
     * Private Methods
     **************************************************************************/
    private ReadOnlyProperties createProp() { return new ReadOnlyProperties(); }

}