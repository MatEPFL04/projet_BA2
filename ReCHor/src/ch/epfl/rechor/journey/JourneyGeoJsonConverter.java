package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;
import java.util.*;

/**
 * This class helps to convert a journey into a GeoJSON document, representing the route taken.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public class JourneyGeoJsonConverter {

    /**
     * Converts the given journey into a GeoJSON whose coordinates follow every departure,
     * intermediate and arrival stop in order.
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
                .map(stop -> {
                    double longitude = Math.round(stop.longitude() * 1e5) / 1e5; // 5 d.p.
                    double latitude = Math.round(stop.latitude()  * 1e5) / 1e5;
                    return (Json) new Json.JArray(List.of(
                            new Json.JNumber(longitude),
                            new Json.JNumber(latitude)
                    ));
                })
                .toList();

        Json.JArray arrayOfPairOfCoordinates = new Json.JArray(listOfCoordinates);

        Map<Json.JString, Json> polyline = new LinkedHashMap<>();
        polyline.put(new Json.JString("type"), new Json.JString("LineString"));
        polyline.put(new Json.JString("coordinates"), arrayOfPairOfCoordinates);

        return new Json.JObject(polyline);
    }
}