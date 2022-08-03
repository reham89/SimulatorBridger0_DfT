package uk.ncl.giacomobergami.components.mel_routing;

import java.util.List;
import java.util.function.BiFunction;

public interface MELRoutingPolicy extends BiFunction<String, List<String>, String> {
}
