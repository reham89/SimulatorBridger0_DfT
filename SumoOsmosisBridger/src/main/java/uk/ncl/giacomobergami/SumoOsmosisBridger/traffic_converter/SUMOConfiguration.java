package uk.ncl.giacomobergami.SumoOsmosisBridger.traffic_converter;

public class SUMOConfiguration {
    public String trace_file;
    public String logger_file;
    public String sumo_program;
    public String sumo_configuration_file_path;
    public String getTrace_file() {
        return trace_file;
    }
    public void setTrace_file(String trace_file) {
        this.trace_file = trace_file;
    }
    public String getLogger_file() {
        return logger_file;
    }
    public void setLogger_file(String logger_file) {
        this.logger_file = logger_file;
    }
    public String getSumo_program() {
        return sumo_program;
    }
    public void setSumo_program(String sumo_program) {
        this.sumo_program = sumo_program;
    }
    public String getSumo_configuration_file_path() {
        return sumo_configuration_file_path;
    }
    public void setSumo_configuration_file_path(String sumo_configuration_file_path) {
        this.sumo_configuration_file_path = sumo_configuration_file_path;
    }
}
