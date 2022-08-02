package uk.ncl.giacomobergami.utils.shared_data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class TimedVehicleMediator {
    CsvMapper csvMapper;
    CsvSchema csvSchema;

    public TimedVehicleMediator() {
        csvMapper = new CsvMapper();
        csvSchema = csvMapper
                .schemaFor(TimedVehicle.class)
                .withHeader();
    }

    public TimedVehicleMediator.CSVWriter beginCSVWrite(File filename) {
        try {
            return new TimedVehicleMediator.CSVWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class CSVReader implements AutoCloseable, Iterator<TimedVehicle> {
        MappingIterator<TimedVehicle> reader;
        CSVReader(File filename) throws IOException {
            reader = csvMapper.readerFor(TimedVehicle.class)
                    .with(csvSchema)
                    .<TimedVehicle>readValues(filename);
        }

        @Override
        public boolean hasNext() {
            return ((reader != null) && (reader.hasNext()));
        }

        @Override
        public TimedVehicle next() {
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

    public TimedVehicleMediator.CSVReader beginCSVRead(File filename) {
        try {
            return new TimedVehicleMediator.CSVReader(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class CSVWriter implements AutoCloseable {
        SequenceWriter writer;
        CSVWriter(File filename) throws IOException {
            writer = csvMapper.writerFor(TimedVehicle.class)
                    .with(csvSchema)
                    .writeValues(filename);
        }

        public boolean write(TimedVehicle object) {
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
            writer.close();
            writer = null;
        }
    }
}
