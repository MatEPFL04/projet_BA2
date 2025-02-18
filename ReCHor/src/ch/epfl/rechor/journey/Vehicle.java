package ch.epfl.rechor.journey;

import java.util.List;

public enum Vehicle {
    TRAM,
    METRO,
    TRAIN,
    BUS,
    FERRY,
    AERIAL_LIFT,
    FUNICULAR;
    static Vehicle[] tableau = values();
    public static final List<Vehicle> ALL =List.of(tableau);

}
