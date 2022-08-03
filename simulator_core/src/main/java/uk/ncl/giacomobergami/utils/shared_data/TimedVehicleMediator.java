package uk.ncl.giacomobergami.utils.shared_data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class TimedVehicleMediator extends CSVMediator<TimedVehicle> {
    public TimedVehicleMediator() {
        super(TimedVehicle.class);
    }
}
