package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class helps to convert a journey into a GeoJSON document, representing the route taken.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public class JourneyGeoJsonConverter {

    /**
     *
     *
     * @param journey the journey to convert into GeoJSON format.
     * @return GeoJson document.
     */
    public static Json toGeoJson(Journey journey) {

        List<Stop> allStops = new ArrayList<>();
        List < Journey.Leg> listLegs = journey.legs();
        for (Journey.Leg leg : listLegs) {
            allStops.add(leg.depStop());
            for (Journey.Leg.IntermediateStop intermediateStops : leg.intermediateStops()) {
                allStops.add(intermediateStops.stop());
            }
        }
        allStops.add(journey.arrStop());

        List<Json> listOfCoordinates = allStops.stream()
                .map(stop -> new Json.JArray(List.of(
                        new Json.JNumber((int) (stop.longitude() * Math.pow(10, 5)) / Math.pow(10, 5)),
                        new Json.JNumber((int) (stop.latitude() * Math.pow(10, 5)) / Math.pow(10, 5))
                )))
                .map(array -> (Json) array)
                .toList();

        Json.JArray arrayOfPairOfCoordinates = new Json.JArray(listOfCoordinates);

        Map<Json.JString, Json> polyline = new LinkedHashMap<>();
        polyline.put(new Json.JString("type"), new Json.JString("LineString"));
        polyline.put(new Json.JString("coordinates"), arrayOfPairOfCoordinates);

        return new Json.JObject(polyline);
    }

}