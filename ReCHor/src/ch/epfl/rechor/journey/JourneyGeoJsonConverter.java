package ch.epfl.rechor.journey;

import ch.epfl.rechor.Json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class helps to convert a journey into a GeoJSON document, representing the route taken.
 *
 * @author Matteo Lazzari (397247)
 * @author Pamphil Nedev (380400)
 */
public class JourneyGeoJsonConverter {

    private final double longitude;
    private final double latitude;

    private JourneyGeoJsonConverter(double longitude, double latitude){
        BigDecimal roundedLon = new BigDecimal(longitude).setScale(5, RoundingMode.HALF_UP);
        BigDecimal roundedLat = new BigDecimal(latitude).setScale(5, RoundingMode.HALF_UP);

        double lonRounded = roundedLon.doubleValue();
        double latRounded = roundedLat.doubleValue();
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     *
     * @param journey the journey to convert into GeoJSON format.
     * @return GeoJson document.
     */
    public static Json toGeoJson(Journey journey) {

        List<Stop> allStops = Stream.concat(
                Stream.of(journey.depStop()),
                Stream.concat(
                        journey.legs().stream()
                                .flatMap(leg -> leg.intermediateStops().stream())
                                .map(Journey.Leg.IntermediateStop::stop),
                        Stream.of(journey.arrStop())
                )
        ).toList();

        List<Json.JArray> listOfCoordinates = allStops.stream()
                .map(stop -> new Json.JArray(List.of(
                        new Json.JNumber(stop.longitude()),
                        new Json.JNumber(stop.latitude())
                )))
                .toList();

        Map<Json.JString, Json> polyline = new HashMap<>();
        polyline.put(new Json.JString("type"), new Json.JString("LineString"));
        polyline.put(new Json.JString("coordinates"), listOfCoordinates);

        return new Json.JObject(polyline);
    }
}