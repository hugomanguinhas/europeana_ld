package pt.ist.oai.harvester.cmd;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.apache.commons.cli.*;

import pt.ist.oai.harvester.*;
import pt.ist.oai.harvester.model.*;
import pt.ist.oai.harvester.model.rw.OAIWriter;
import pt.ist.util.iterator.CloseableIterable;

public class ListRecordsCmd extends VerbCmd
{
    @Override
    protected String getVerb() { return "ListRecords"; }

    @Override
    @SuppressWarnings("static-access")
    protected Options buildOptions()
    {
        Options options = new Options();
        options.addOption(new Option("help", getProperty("info.Verbs.help")));
        options.addOption(OptionBuilder.withArgName("host")
                .hasArg()
                .withDescription(getProperty("info.Verbs.host"))
                .isRequired()
                .create("host"));
        options.addOption(OptionBuilder.withArgName("from")
                .hasArg()
                .withDescription(getProperty("info.ListRecords.from"))
                .create("from"));
        options.addOption(OptionBuilder.withArgName("until")
                .hasArg()
                .withDescription(getProperty("info.ListRecords.until"))
                .create("until"));
        options.addOption(OptionBuilder.withArgName("set")
                .hasArg()
                .withDescription(getProperty("info.ListRecords.set"))
                .create("set"));
        options.addOption(OptionBuilder.withArgName("metadataPrefix")
                .hasArg()
                .isRequired()
                .withDescription(getProperty("info.ListRecords.metadataPrefix"))
                .create("metadataPrefix"));
        //'screen'|'zip'|'dir'|'xml'|'xml-zip'
        options.addOption(OptionBuilder.withArgName("output")
                .hasArg()
                .withDescription(getProperty("info.ListRecords.output"))
                .create("output"));
        options.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription(getProperty("info.ListRecords.file"))
                .create("file"));
        options.addOption(OptionBuilder.withArgName("'record-only'|'both'")
                .hasArg()
                .withDescription(getProperty("info.ListRecords.detail"))
                .create("detail"));
        return options;
    }

    @Override
    protected void process(CommandLine line, PrintStream print)
              throws Throwable
    {
        long elapsed = System.currentTimeMillis();
        long records = 0;

        OAIHarvester harvester = new OAIHarvesterImpl(line.getOptionValue("host"));
        Properties properties = getProperties(line, "from", "until", "set", "metadataPrefix");

        //Handle details
        DetailSupport detailSupport;
        String detail = line.getOptionValue("detail", "record-only");
        if(detail.equals("both")) {
            detailSupport = new BothDetailSupport(harvester.identify());
        }
        else {
            detailSupport = new RecordOnlyDetailSupport();
        }

        String outputType = line.getOptionValue("output", "screen");

        //Print result to a compressed zip file
        if(outputType.equals("zip")) {
            if(!line.hasOption("file")) return;
            String path = line.getOptionValue("file");
            if(!path.endsWith(".zip")) path = path + ".zip";
            File output = new File(path).getAbsoluteFile();

            print.println("   output: compressed zip file");
            print.println(" location: " + output.getAbsolutePath());
            ensureDir(output.getParentFile());

            CloseableIterable<OAIRecord> iterable = harvester.iterateRecords(properties);
            try {
                ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(output));
                try {
                    for(OAIRecord record : iterable) {
                        records++;
                        ZipEntry je = new ZipEntry("record" + records + ".xml");
                        detailSupport.writeToZip(zip, record, je);
                    }
                }
                finally { zip.close(); }
            }
            finally { iterable.close(); }
        }
        else if(outputType.equals("dir")) {
            if(!line.hasOption("file")) return;
            String path = line.getOptionValue("file");
            File output = new File(path).getAbsoluteFile();
            ensureDir(output);
            if(output.isDirectory()) {
                print.println("   output: directory");
                print.println(" location: " + output.getAbsolutePath());
                CloseableIterable<OAIRecord> iterable = harvester.iterateRecords(properties);
                try {
                    for(OAIRecord record : iterable) {
                        records++;
                        String name = "record" + records + ".xml";
                        detailSupport.writeToFile(record, new File(output, name));
                    }
                }
                finally {
                    iterable.close();
                }
            }
        }
        else if(outputType.equals("xml")) {
            if(!line.hasOption("file")) return;
            String path = line.getOptionValue("file");
            File output = new File(path).getAbsoluteFile();
            ensureDir(output.getParentFile());

            print.println("   output: single xml file");
            print.println(" location: " + output.getAbsolutePath());
            OAIRequest<CloseableIterable<OAIRecord>> request = harvester.newIterateRecords(properties);
            records = new OAIWriter().write(request, null, new StreamResult(output));
        }
        else if(outputType.equals("xml-zip")) {
            if(!line.hasOption("file")) return;
            String path = line.getOptionValue("file");
            File output = new File(path).getAbsoluteFile();
            ensureDir(output.getParentFile());

            print.println("   output: compressed single xml file");
            print.println(" location: " + output.getAbsolutePath());
            OAIRequest<CloseableIterable<OAIRecord>> request = harvester.newIterateRecords(properties);
            ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(output));
            try {
                ZipEntry je = new ZipEntry("records.xml");
                zip.putNextEntry(je);
                try {
                    records = new OAIWriter().write(request, null, new StreamResult(zip));
                }
                finally {
                    zip.closeEntry();
                }
            }
            finally {
                zip.close();
            }
        }
        else {
            print.println("   output: screen");
            CloseableIterable<OAIRecord> iterable = harvester.iterateRecords(properties);
            try {
                for(OAIRecord record : iterable) {
                    records++;
                    detailSupport.writeToScreen(record, print);
                }
            }
            finally {
                iterable.close();
            }
        }
        elapsed = System.currentTimeMillis() - elapsed;
        print.println();
        print.println("Successfuly retrieved " + records + " record(s) in " + elapsed + "ms");
    }

    protected abstract class DetailSupport
    {
        protected ByteArrayOutputStream _output = new ByteArrayOutputStream();

        public abstract void writeToFile(OAIRecord record, File file)
               throws IOException;

        public abstract void writeToZip(ZipOutputStream zipOutput
                                      , OAIRecord record, ZipEntry ze)
               throws IOException;

        public abstract void writeToScreen(OAIRecord record, PrintStream ps)
               throws IOException;
    }

    protected class RecordOnlyDetailSupport extends DetailSupport
    {
        protected ModelPrinter _printer = new ModelPrinter();

        @Override
        public void writeToZip(ZipOutputStream zipOutput, OAIRecord record
                             , ZipEntry ze) throws IOException
        {
            if(!record.hasMetadata()) { return; }
            try {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.transform(new DOMSource(record.getMetadata()), new StreamResult(_output));
                _output.flush();
                zipOutput.putNextEntry(ze);
                try     { _output.writeTo(zipOutput); }
                finally { zipOutput.closeEntry();     }
            }
            catch (TransformerException |
                   TransformerFactoryConfigurationError e)
            {
                throw new IOException(e);
            }
            finally { _output.reset(); }
        }

        @Override
        public void writeToScreen(OAIRecord record, PrintStream ps)
               throws IOException
        {
            if(!record.hasMetadata()) { return; }
            _printer.print(record.getMetadata(), ps);
            ps.println();
        }

        @Override
        public void writeToFile(OAIRecord record, File file) throws IOException
        {
            if(!record.hasMetadata()) { return; }
            try {
                Transformer t = TransformerFactory.newInstance().newTransformer();
                t.transform(new DOMSource(record.getMetadata()), new StreamResult(file));
            }
            catch(TransformerException | TransformerFactoryConfigurationError e)
            {
                throw new IOException(e);
            }
        }
    }

    protected class BothDetailSupport extends DetailSupport
    {
        protected OAIWriter     _writer;
        protected OAIDataSource _source;

        public BothDetailSupport(OAIDataSource source)
        {
            _source = source;
            _writer = new OAIWriter();
        }

        @Override
        public void writeToZip(ZipOutputStream zipOutput, OAIRecord record, ZipEntry ze) throws IOException {
            try {
                _writer.write(record, _source, new StreamResult(_output));
                _output.flush();
                zipOutput.putNextEntry(ze);
                try     { _output.writeTo(zipOutput); }
                finally { zipOutput.closeEntry();     }
            }
            catch (TransformerFactoryConfigurationError e) {
                throw new IOException(e);
            }
            finally { _output.reset(); }
        }
        
        @Override
        public void writeToScreen(OAIRecord record, PrintStream ps)
               throws IOException
        {
            _writer.write(record, _source, new StreamResult(ps));
            ps.println();
        }

        @Override
        public void writeToFile(OAIRecord record, File file) throws IOException
        {
            _writer.write(record, _source, new StreamResult(file));
        }
    }

    public static void main(String[] args)
    {
        new ListRecordsCmd().process(args);
    }
}
