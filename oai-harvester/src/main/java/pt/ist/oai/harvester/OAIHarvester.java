package pt.ist.oai.harvester;

import java.util.*;

import pt.ist.oai.harvester.exceptions.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.util.iterator.*;

public interface OAIHarvester
{
    //Identity
    public OAIDataSource identify()
           throws OAIException;

    //List Metadata Formats
    public List<OAIMetadataFormat> listMetadataFormats(
           Properties properties)
           throws OAIException;

    public List<OAIMetadataFormat> listMetadataFormats()
           throws OAIException;

    public List<OAIMetadataFormat> listMetadataFormats(
           String itemIdentifier)
           throws OAIException;

    //List Identifiers
    public List<OAIRecordHeader> listIdentifiers(
           Properties properties)
           throws OAIException;

    public List<OAIRecordHeader> listIdentifiers(
           String set)
           throws OAIException;

    public List<OAIRecordHeader> listIdentifiers(
           String set, String metadataPrefix)
           throws OAIException;

    public List<OAIRecordHeader> listIdentifiers(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException;

    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           Properties properties)
           throws OAIException;

    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           String set) 
           throws OAIException;

    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           String set, String metadataPrefix)
           throws OAIException;

    public CloseableIterable<OAIRecordHeader> iterateIdentifiers(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException;

    //List Sets
    public List<OAIMetadataSet> listSets()
           throws OAIException;

    public CloseableIterable<OAIMetadataSet> iterateSets()
           throws OAIException;

    //Get Record
    public OAIRecord getRecord(
           Properties properties)
           throws OAIException;

    public OAIRecord getRecord(
           String id, String metadataPrefix)
           throws OAIException;

    //List Records
    public List<OAIRecord> listRecords(
           Properties properties)
           throws OAIException;

    public List<OAIRecord> listRecords(
           String metadataPrefix)
           throws OAIException;

    public List<OAIRecord> listRecords(
           String set, String metadataPrefix)
           throws OAIException;

    public List<OAIRecord> listRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException;

    //Iterate Records
    public CloseableIterable<OAIRecord> iterateRecords(
           Properties properties)
           throws OAIException;

    public CloseableIterable<OAIRecord> iterateRecords(
           String metadataPrefix)
           throws OAIException;

    public CloseableIterable<OAIRecord> iterateRecords(
           String set, String metadataPrefix)
           throws OAIException;

    public CloseableIterable<OAIRecord> iterateRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException;

    //Count Records
    public long countRecords(
           Properties properties)
           throws OAIException;

    public long countRecords(
           String metadataPrefix)
           throws OAIException;

    public long countRecords(
           String set, String metadataPrefix)
           throws OAIException;

    public long countRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException;

    //List Metadata Formats
    public OAIRequest<List<OAIMetadataFormat>> newListMetadataFormats();

    public OAIRequest<List<OAIMetadataFormat>> newListMetadataFormats(
           Properties properties);

    public OAIRequest<List<OAIMetadataFormat>> newListMetadataFormats(
           String itemIdentifier);

    //List Identifiers
    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           Properties properties);

    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           String set);

    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           String set, String metadataPrefix);

    public OAIRequest<List<OAIRecordHeader>> newListIdentifiers(
           String set, String metadataPrefix, Date from, Date to);


    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           Properties properties);

    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           String set);

    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           String set, String metadataPrefix);

    public OAIRequest<CloseableIterable<OAIRecordHeader>> newIterateIdentifiers(
           String set, String metadataPrefix, Date from, Date to);

    //List Sets
    public OAIRequest<List<OAIMetadataSet>> newListSets();

    public OAIRequest<CloseableIterable<OAIMetadataSet>> newIterateSets();

    //Get Record
    public OAIRequest<OAIRecord>       newGetRecord(
           Properties properties);
    public OAIRequest<OAIRecord>       newGetRecord(
           String id, String metadataPrefix);

    //List Records
    public OAIRequest<List<OAIRecord>> newListRecords(
           Properties properties);

    public OAIRequest<List<OAIRecord>> newListRecords(
           String metadataPrefix);

    public OAIRequest<List<OAIRecord>> newListRecords(
           String set, String metadataPrefix);

    public OAIRequest<List<OAIRecord>> newListRecords(
           String set, String metadataPrefix, Date from, Date to);

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           Properties properties);

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           String metadataPrefix);

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           String set, String metadataPrefix);

    public OAIRequest<CloseableIterable<OAIRecord>> newIterateRecords(
           String set, String metadataPrefix, Date from, Date to);

    //Count Records
    public OAIRequest<Long> newCountRecords(
           Properties properties)
           throws OAIException;

    public OAIRequest<Long> newCountRecords(
           String metadataPrefix)
           throws OAIException;

    public OAIRequest<Long> newCountRecords(
           String set, String metadataPrefix)
           throws OAIException;

    public OAIRequest<Long> newCountRecords(
           String set, String metadataPrefix, Date from, Date to)
           throws OAIException;
}