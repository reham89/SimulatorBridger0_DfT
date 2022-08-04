package uk.ncl.giacomobergami.utils.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JSON {
    private static Gson gson = new Gson();

    public static <T> List<T> stringToArray(File path, Class<T[]> clazz) {
        Reader reader;
        try {
            reader = Files.newBufferedReader(path.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        var arr = Arrays.asList(gson.fromJson(reader, clazz));
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr; //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }


}
