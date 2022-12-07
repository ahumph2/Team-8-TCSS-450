package edu.uw.tcss450.tcss450group82022.model;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Objects;

/**
 * Object to store weather information for a given location.
 *
 
 */
public class WeatherProfile implements Serializable {

//    /** Radius of Earth in miles. */
//    private static final double EARTH_RADIUS = 3958.75;
//    /** Minimum distance (in miles) for two locations to be considered far enough apart
//     *  to be considered different places.*/
//    private static final double WEATHER_RADIUS = 25.0;

    /** latitude of location */
    private double mLatitude;
    /** longitude of location */
    private double mLongitude;
    /** "{CityName}, {StateName}" of location */
    private String mLocationStr;
    /** Current conditions JSON from weather API */
    private String mCurrentWeatherJSONStr;
    /** 10 Day forecast info JSON from weather API */
    private String m7DayForecastJSONStr;
    /** 24 Hour forecast info JSON from weather API */
    private String m48hrForecastJSONStr;

    /**
     * Constructor
     *
     * @param tLoc          latitiude & longitude that weather relates to.
     * @param tCurWeather   current conditions JSON string.
     * @param t7day        10 day forecast JSON string.
     * @param t48hr         24 hour forecast JSON string.
     * @param tLocStr    formatted city and state of location
     */
    public WeatherProfile( final LatLng tLoc,
                    final String tCurWeather,
                    final String t7day,
                    final String t48hr,
                    final String tLocStr) {

        mLatitude = tLoc.latitude;
        mLongitude = tLoc.longitude;
        mCurrentWeatherJSONStr = tCurWeather;
        m7DayForecastJSONStr = t7day;
        m48hrForecastJSONStr = t48hr;
        mLocationStr = tLocStr;
    }

    /** @return latitude and longitude of location as LatLng object. */
    public LatLng getLocation() { return new LatLng(mLatitude, mLongitude); }
    /** @return JSON string for current weather conditions. */
    public String getCurrentWeather() { return mCurrentWeatherJSONStr; }
    /** @return JSON string for 10 day forecast information. */
    public String get7DayForecast() { return m7DayForecastJSONStr; }
    /** @return JSON string for 24 hour forecast information. */
    public String get48hrForecast() { return m48hrForecastJSONStr; }
    /** @return "{CityName}, {StateName}" */
    public String getLocationStr() { return mLocationStr; }

    /** @return true if the weather profiles have the same mCityState field; false otherwise. */
    @Override
    public boolean equals(@Nullable Object theOther) {
        if(!(theOther instanceof WeatherProfile)) { return false; }
        WeatherProfile other = (WeatherProfile) theOther;

        return mLocationStr.equals(Objects.requireNonNull(other).getLocationStr());
    }

//    /**
//     * @return true if the location of the weather profile passed in is within 25 miles
//     *         of this one; false otherwise
//     */
//    public boolean isCloseTo(double lat, double lng) {
//        LatLng other = new LatLng(lat, lng);
//        return distanceBetween(this.getLocation(), other) < WEATHER_RADIUS;
//    }
//
//    /**
//     * Calculates distance between two points on the globe, in miles, using the Haversine Formula.
//     *
//     * @param a location a
//     * @param b location b
//     * @return the distance between the two input locations, in miles.
//     */
//    private Double distanceBetween(LatLng a, LatLng b) {
//        double latDelta = Math.toRadians(b.latitude - a.latitude);
//        double lngDelta = Math.toRadians(b.longitude - a.longitude);
//
//        double sinLatDelta = Math.sin(latDelta / 2);
//        double sinLngDelta = Math.sin(lngDelta / 2);
//
//        double x = Math.pow(sinLatDelta, 2) + Math.pow(sinLngDelta, 2)
//                * Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude));
//
//        double y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1-x));
//
//        return EARTH_RADIUS * y;
//    }
}
