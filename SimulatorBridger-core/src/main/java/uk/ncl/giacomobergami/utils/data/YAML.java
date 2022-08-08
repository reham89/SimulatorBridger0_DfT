package uk.ncl.giacomobergami.utils.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class YAML {
    public static <T> Optional<T> parse(Class<T> clazz, File f) {
        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            return Optional.of(mapper.readValue(f, clazz));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }
    public static <T> boolean serialize(T object, File f) {
        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            mapper.writeValue(f, object);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
