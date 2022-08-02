package uk.ncl.giacomobergami.utils.structures;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class StraightforwardAdjacencyList<K> {

    public static class Edge<K> {
        public K key;
        public K value;

        public K getKey() {
            return key;
        }
        public void setKey(K key) {
            this.key = key;
        }
        public K getValue() {
            return value;
        }
        public void setValue(K value) {
            this.value = value;
        }

        public Edge() {
            this.key = null;
            this.value = null;
        }

        public Edge(K key, K value) {
            this.key = key;
            this.value = value;
        }
    }


    SetMultimap<K, K> m;
    Edge<K> bogusEdgeForClass;
    Edge<String> bogusStringEdgeForClass;

    public SetMultimap<K, K> getM() {
        return m;
    }

    public void setM(SetMultimap<K, K> m) {
        this.m = m;
    }

    public StraightforwardAdjacencyList() {
        m = HashMultimap.create();
        bogusEdgeForClass = new Edge<>(null, null);
        bogusStringEdgeForClass = new Edge<>(null, null);
    }
    public boolean put(K src, K dst) {
        return m.put(src, dst);
    }
    public boolean hasEdge(K src, K dst) {
        return m.containsEntry(src, dst);
    }
    public boolean removeEdge(K src, K dst) {
        return m.remove(src, dst);
    }
    public boolean removeNodeCompletely(K src) {
        var set = m.removeAll(src);
        return m.entries().removeIf(x ->x.getValue().equals(src)) || (set != null && (!set.isEmpty()));
    }

    public Set<Map.Entry<K, Collection<K>>> entrySet() {
        return m.asMap().entrySet();
    }

    public Iterator<Edge<K>> asIterator() {
        return new Iterator<>() {
            Iterator<Map.Entry<K, K>> it = m.entries().iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
            @Override
            public Edge<K> next() {
                var local = it.next();
                if (local == null) return null;
                return new Edge<K>(local.getKey(), local.getValue());
            }
        };
    }

    public void forEach(BiConsumer<K, K> consumer) {
        if (consumer!=  null) {
            m.entries().forEach(x -> consumer.accept(x.getKey(), x.getValue()));
        }
    }

    public void forEach(Consumer<Map.Entry<K, K>> consumer) {
        if (consumer!=  null) {
            m.entries().forEach(consumer);
        }
    }

    public boolean dump(File filename, Function<K, String> mapper) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(bogusStringEdgeForClass.getClass())
                .withHeader();
        SequenceWriter writer = null;
        try {
            writer = csvMapper.writerFor(bogusStringEdgeForClass.getClass())
                    .with(csvSchema)
                    .writeValues(filename.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        var it = asIterator();
        while (it.hasNext()) {
            try {
                var curr = it.next();
                writer.write(new Edge<>(mapper.apply(curr.key), mapper.apply(curr.value)));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean dump(File filename)  {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(bogusEdgeForClass.getClass())
                .withHeader();
        SequenceWriter writer = null;
        try {
            writer = csvMapper.writerFor(bogusEdgeForClass.getClass())
                    .with(csvSchema)
                    .writeValues(filename.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        var it = asIterator();
        while (it.hasNext()) {
            try {
                writer.write(it.next());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void clear() {
        m.clear();
    }

    public boolean addAll(File filename) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(bogusEdgeForClass.getClass())
                .withHeader();
        MappingIterator<Edge<K>> reader;
        try {
            reader = csvMapper.readerFor(bogusEdgeForClass.getClass())
                    .with(csvSchema)
                    .<Edge<K>>readValues(filename.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        while ((reader != null) && (reader.hasNext())) {
            var x=  reader.next();
            put(x.key, x.value);
        }
        return true;
    }

    public boolean addAll(File filename, Function<String, K> unmarshaller) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper
                .schemaFor(bogusStringEdgeForClass.getClass())
                .withHeader();
        MappingIterator<Edge<String>> reader;
        try {
            reader = csvMapper.readerFor(bogusStringEdgeForClass.getClass())
                    .with(csvSchema)
                    .<Edge<String>>readValues(filename.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        while ((reader != null) && (reader.hasNext())) {
            var x=  reader.next();
            put(unmarshaller.apply(x.key), unmarshaller.apply(x.value));
        }
        return true;
    }

//    public static void main(String[] test) {
//        {
//            StraightforwardAdjacencyList<Integer> adj = new StraightforwardAdjacencyList<>();
//            adj.put(0, 1);
//            adj.put(0, 2);
//            adj.put(0, 3);
//            adj.put(2, 3);
//            adj.put(2, 3);
//            adj.put(2, 4);
//            adj.dump(new File("adj.csv").getAbsoluteFile());
//        }
//        {
//            StraightforwardAdjacencyList<Integer> adj = new StraightforwardAdjacencyList<>();
//            adj.addAll(new File("adj.csv"));
//            adj.forEach(System.out::println);
//        }
//    }

}
