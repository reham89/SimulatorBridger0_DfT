package uk.ncl.giacomobergami.components;

import java.util.List;
import java.util.function.BiFunction;

public interface MELRoutingPolicy extends BiFunction<String, List<String>, String> {
}
