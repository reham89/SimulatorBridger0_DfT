package uk.ncl.giacomobergami.utils.shared_data.iot;

import uk.ncl.giacomobergami.utils.data.CSVMediator;

public class TimedIoTMediator extends CSVMediator<TimedIoT> {
    public TimedIoTMediator() {
        super(TimedIoT.class);
    }
}
