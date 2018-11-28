package com.bytes.fightr.client.util;

import android.location.Location;

/**
 * Created by Kent on 10/21/2015.
 */
public class LocationUtils {

    public static String toString(Location location) {
        return String.format(
                "Provider:  %1$s " + System.lineSeparator() +
                        "Accuracy:  %2$f " + System.lineSeparator() +
                        "Latitude:  %3$f " + System.lineSeparator() +
                        "Longitude: %4$f " + System.lineSeparator() +
                        "Altitude:  %5$f " + System.lineSeparator() +
                        "Bearing:   %6$f " + System.lineSeparator() +
                        "Speed:     %7$f " + System.lineSeparator() +
                        "Time:      %8$d " + System.lineSeparator(),
                location.getProvider(),  // string
                location.getAccuracy(),  // float
                location.getLatitude(),  // double
                location.getLongitude(), // double
                location.getAltitude(),  // double
                location.getBearing(),   // float
                location.getSpeed(),     // float
                location.getTime());     // long
    }

    public static String toShortString(Location location) {
        return String.format(
                "Accuracy:  %1$f " + System.lineSeparator() +
                "Latitude:  %2$f " + System.lineSeparator() +
                "Longitude: %3$f " + System.lineSeparator() +
                "Altitude:  %4$f " + System.lineSeparator(),
                location.getAccuracy(),  // float
                location.getLatitude(),  // double
                location.getLongitude(), // double
                location.getAltitude()); // double
    }
}
