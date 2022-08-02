package uk.ncl.giacomobergami.utils.shared_data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class RSUMediator {
    CsvMapper csvMapper;
    CsvSchema csvSchema;

    public RSUMediator() {
        csvMapper = new CsvMapper();
        csvSchema = csvMapper
                .schemaFor(RSU.class)
                .withHeader();
    }

    public CSVWriter beginCSVWrite(File filename) {
        try {
            return new CSVWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CSVReader beginCSVRead(File filename) {
        try {
            return new CSVReader(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class CSVReader implements AutoCloseable, Iterator<RSU> {
        MappingIterator<RSU> reader;
        CSVReader(File filename) throws IOException {
            System.out.println(filename.getAbsolutePath());
            reader = csvMapper.readerFor(RSU.class)
                    .with(csvSchema)
                    .<RSU>readValues(filename.getAbsoluteFile());
        }

        @Override
        public boolean hasNext() {
            return ((reader != null) && (reader.hasNext()));
        }

        @Override
        public RSU next() {
            if ((reader == null) || (!reader.hasNext())) return null;
            return reader.next();
        }

        @Override
        public void close() throws Exception {
            if (reader != null) {
                reader.close();
                reader = null;
            }
        }
    }

    public class CSVWriter implements AutoCloseable {
        SequenceWriter writer;
        CSVWriter(File filename) throws IOException {
            writer = csvMapper.writerFor(RSU.class)
                    .with(csvSchema)
                    .writeValues(filename.getAbsoluteFile());
        }

        public boolean write(RSU object) {
            if (writer == null) return false;
            try {
                writer.write(object);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        public void close() throws Exception {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }
    }


}
