package edu.uw.tcss450.tcss450group82022.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.model.LocationViewModel;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfile;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfileViewModel;

/** Collection of static utility methods used in multiple places throughout the app. */
public class Utils {

    /** Shouldn't be able to instantiate. */
    private Utils() {}

    /**
     * Checks if weather information in WeatherProfileViewModel needs to be updated so that requests to
     * the API aren't made every time a fragment with weather info is loaded. Weather info is considered outdated if:
     *   - There is no weather info (duh).
     *   - the timestamp is before the top of the last hour (e.g. current time is 5:10 but timestamp is before 5:00)
     * @param theWeatherVM most recent weather information.
     */
    public static void updateWeatherIfNecessary(WeatherProfileViewModel theWeatherVM) {
        ArrayList<LatLng> locationsToUpdate = new ArrayList<>();
        // Redundant code in branches because method will be called a lot without hitting them.
        if(theWeatherVM.getCurrentLocationWeatherProfile().getValue() == null) {
            // Add current location to list of locations to update
            Location curLoc = Objects.requireNonNull(LocationViewModel
                    .getFactory()
                    .create(LocationViewModel.class)
                    .getCurrentLocation()
                    .getValue());
            locationsToUpdate.add(new LatLng(curLoc.getLatitude(), curLoc.getLongitude()));

            theWeatherVM.update(locationsToUpdate);

        } else if(theWeatherVM.getTimeStamp() < Utils.getTopOfLastHour()) {
            // Add current location to list of locations to update first:
            Location curLoc = LocationViewModel
                    .getFactory()
                    .create(LocationViewModel.class)
                    .getCurrentLocation()
                    .getValue();
            if(curLoc != null) {locationsToUpdate.add(new LatLng(curLoc.getLatitude(), curLoc.getLongitude()));}

            // Add saved locations from VM to list of locations to update
            if(theWeatherVM.getSavedLocationWeatherProfiles().getValue() != null) {
                for(WeatherProfile wp : theWeatherVM.getSavedLocationWeatherProfiles().getValue()) {
                    locationsToUpdate.add(wp.getLocation());
                }

                theWeatherVM.update(locationsToUpdate);
            }
        }
    }

    public static Address getAddressFromLocation(Double tLat, Double tLon, Context tContext) throws IOException {
        Address result = null;
        Geocoder geo = new Geocoder(tContext, Locale.getDefault());
        List<Address> addresses = geo.getFromLocation(tLat, tLon, 1);
        if(addresses != null && !addresses.isEmpty()) {
            result = addresses.get(0);
        }
        return result;
    }

    public static Address getAddressFromLocation(String tZip, Context tContext) throws IOException {
        Address result = null;
        Geocoder geo = new Geocoder(tContext, Locale.getDefault());
        List<Address> addresses = geo.getFromLocationName(tZip, 1);
        if(addresses != null && !addresses.isEmpty()) {
            result = addresses.get(0);
        }
        return result;
    }

    /**
     * Formats the location of the provided address into a string.
     * @param tAddr the address from the Geocoder
     * @return      the formatted, concatenated string.
     */
    public static String getFormattedLocation(Address tAddr) {
        StringBuilder result = new StringBuilder();
        String first;
        String second;
        boolean useCountry = true;

        //Handle City/State;
        if(tAddr.getLocality() != null) {//Use city if available
            first = tAddr.getLocality() + ", ";
            useCountry = false;
        } else if(tAddr.getAdminArea() != null) {//Use "state" if no city
            first = tAddr.getAdminArea() + ", ";
        } else {//Otherwise just use coordinates
            DecimalFormat df = new DecimalFormat("##.##");
            first = df.format(tAddr.getLatitude()) + ":" + df.format(tAddr.getLongitude()) + ", ";
        }

        //Handle State/Country depending on City/State:
        if(useCountry) {
            second = tAddr.getCountryName();
        } else { //Use state abbreviation if in US; country name otherwise
            String state = getStateAbbr(tAddr.getAdminArea());
            second = state == null ? tAddr.getCountryName() : state;
        }

        result.append(first).append(second).append('\u00A0');
        return result.toString();
    }

    /** @return the current time, rounded down to the last hour. */
    private static long getTopOfLastHour() {
        return (System.currentTimeMillis() / 1000L) - ((System.currentTimeMillis() / 1000L) % 3600);
    }

    /**
     * Manually hides soft keyboard.
     * @param activity context.
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String jadenCase(String str) {
        String[] strArr = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for(String word : strArr) {
            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            sb.append(cap);
            sb.append(" ");
        }
        sb.trimToSize();
        return sb.toString();
    }

    public static String getDisplayTemp(Double tNumber, String tUnit) {
        DecimalFormat df = new DecimalFormat("###");
        double result = tNumber - 273.15;
        if("f".equals(tUnit.toLowerCase())) {
            result = result * 9 / 5 + 32;
        }
        return df.format(result);
    }

    private static String getStateAbbr(String fullName) {
        Map<String, String> states = new HashMap<>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","QC");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");

        return states.get(fullName);
    }
}