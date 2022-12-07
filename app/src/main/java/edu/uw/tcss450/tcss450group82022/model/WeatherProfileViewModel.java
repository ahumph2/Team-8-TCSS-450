package edu.uw.tcss450.tcss450group82022.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.utils.GetAsyncTask;
import edu.uw.tcss450.tcss450group82022.utils.Utils;

/**
 * ViewModel for keeping track of current, selected & saved location weather profiles
 * throughout the lifecycle of the app while user is logged in.
 *
 
 */
public class WeatherProfileViewModel extends AndroidViewModel {

    /** Current instance of ViewModel with most recent weather profile info. */
    private static WeatherProfileViewModel mInstance;

    /** Keeps track of the device's location weather profile. */
    private MutableLiveData<WeatherProfile> mCurrentLocationWeatherProfile;
    /** Keeps track of the weather profile selected by the user (e.g. through zip code search */
    private MutableLiveData<WeatherProfile> mSelectedLocationWeatherProfile;
    /** Keeps Keeps track of weather profiles for user's saved locations. */
    private MutableLiveData<List<WeatherProfile>> mSavedLocationsWeatherProfiles;

    /** UNIX timestamp, in seconds, for last time weather profiles were updated. */
    private long mLastUpdated;
    /** Keeps track of saved location list passed into update method for use in post method of AsyncTask */
    private ArrayList<LatLng> mSavedLocations;
    /** Holds response JSON from API call until all calls are ready to process.**/
    private ArrayList<String> mRawWeatherData;

    /**
     * Private constructor instantiates MutableLiveData objects and sets timestamp to now.
     * @param theApp instance of application
     */
    private WeatherProfileViewModel(Application theApp) {
        super(theApp);
        mCurrentLocationWeatherProfile = new MutableLiveData<>();
        mSelectedLocationWeatherProfile = new MutableLiveData<>();
        mSavedLocationsWeatherProfiles = new MutableLiveData<>();
        mRawWeatherData = new ArrayList<>();
        mLastUpdated = System.currentTimeMillis() / 1000L;
    }

    // Public methods
    /**
     * Makes API call to update weather profiles for current and/or saved locations.
     * @param theLocationsToUpdate list of locations to get weather info for.
     */
    public void update(ArrayList<LatLng> theLocationsToUpdate) {
        if(theLocationsToUpdate.size() > 0) {
            mSavedLocations = theLocationsToUpdate;
            for(LatLng loc : mSavedLocations) {
                Uri uri = new Uri.Builder()
                        .scheme("https")
                        .authority("tcss450-2022au-group8.herokuapp.com")
                        .appendPath("weather")
                        .appendPath(Double.toString(loc.latitude) + ':' + loc.longitude)
                        .build();

                Log.d("WEATHER_API_CALL", uri.toString());

                new GetAsyncTask.Builder(uri.toString())
                        .onPostExecute(this::fetchWeatherPost)
                        .onCancelled(error -> Log.e("", error))
                        .build().execute();
            }
        } else {
            Log.d("WEATHER_ERR", "Unable to get device location & no saved locations");
        }
    }

    /**
     * Saves given location to list of saved locations.
     * @param theWP weather profile to save.
     */
    public void saveLocation(final WeatherProfile theWP) {
        List<WeatherProfile> savedLocations = mSavedLocationsWeatherProfiles.getValue();
        Objects.requireNonNull(savedLocations).add(theWP);
        mSavedLocationsWeatherProfiles.setValue(savedLocations);
        saveToSharedPrefs();
    }

    /**
     * Removes specified location from list of saved locations.
     * @param idx index of saved location to remove in list.
     */
    public void removeLocation(final int idx) {
        List<WeatherProfile> savedLocations = mSavedLocationsWeatherProfiles.getValue();
        Objects.requireNonNull(savedLocations).remove(idx);
        mSavedLocationsWeatherProfiles.setValue(savedLocations);
        saveToSharedPrefs();
    }

    //Getters
    /** @return current location weather profile. */
    public LiveData<WeatherProfile> getCurrentLocationWeatherProfile() {return mCurrentLocationWeatherProfile;}

    /** @return selected location weather profile. */
    public LiveData<WeatherProfile> getSelectedLocationWeatherProfile() {return mSelectedLocationWeatherProfile;}

    /** @return list of saved location weather profiles. */
    public LiveData<List<WeatherProfile>> getSavedLocationWeatherProfiles() {return mSavedLocationsWeatherProfiles;}

    /** @return UNIX timestamp from when instance was instantiated.*/
    public long getTimeStamp() { return mLastUpdated; }

    /**
     * Updates weather profile for user selection (through zip search, map selection or from saved locations).
     * @param theWP new weather profile.
     */
    public void setSelectedLocationWeatherProfile(final WeatherProfile theWP) {mSelectedLocationWeatherProfile.setValue(theWP);}

    /**
     * Updates weather profile for device location and updates shared preferences to reflect change.
     * @param theWP new weather profile.
     */
    private void setCurrentLocationWeatherProfile(final WeatherProfile theWP) {
        mCurrentLocationWeatherProfile.setValue(theWP);
        saveToSharedPrefs();
    }

    /**
     * Updates weather profiles for saved locations and also shared preferences to reflect change.
     * @param theWPs list of updated weather profiles.
     */
    private void setSavedLocationWeatherProfile(final ArrayList<WeatherProfile> theWPs) {mSavedLocationsWeatherProfiles.setValue(theWPs);}

    /**
     * Updates timestamp of last weather update.
     * @param theTime UNIX timestamp in seconds.
     */
    private void setTimeStamp(final long theTime) {
        mLastUpdated = theTime;
        saveToSharedPrefs();
    }

    //Private helpers
    /**
     * Post execute for AsyncTask that queues responses for current and saved locations before
     * calling processWeatherData to parse data.
     * @param result JSON response.
     */
    private void fetchWeatherPost(final String result) {
        mRawWeatherData.add(result);
        if(mRawWeatherData.size() == mSavedLocations.size()) {
            processWeatherData();
        }
    }

    /**
     * Helper method for fetchWeatherPost that gets weather info from API. Parses JSON response and
     * sets up weather profiles for current and saved locations, then updates live data, timestamp
     * & shared preferences.
     */
    private void processWeatherData() {
        ArrayList<WeatherProfile> savedLocationWeatherProfileList = new ArrayList<>();

        for(int i = 0; i < mRawWeatherData.size(); i++) {
            String raw = mRawWeatherData.get(i);
            LatLng currSavedLoc = mSavedLocations.get(i);
            try {
                JSONObject root = new JSONObject(raw);

                String currJSONStr = root.getJSONObject("current").toString();
                String dailyJSONStr = root.getJSONArray("daily").toString();
                String hourlyJSONStr = root.getJSONArray("hourly").toString();

                Address addr = Utils.getAddressFromLocation(root.getDouble("lat"), root.getDouble("lon"), getApplication());
                String locationStr = Utils.getFormattedLocation(addr);

                WeatherProfile wp = new WeatherProfile(currSavedLoc, currJSONStr, dailyJSONStr, hourlyJSONStr, locationStr);

                // First block of weather info is always current location
                if(i == 0) {
                    mCurrentLocationWeatherProfile.setValue(wp);
                } else {
                    savedLocationWeatherProfileList.add(wp);
                }

                mSavedLocationsWeatherProfiles.setValue(savedLocationWeatherProfileList);
                mLastUpdated = System.currentTimeMillis() / 1000L;

                // Save updated WeatherProfileVM info to sharedPrefs:
                saveToSharedPrefs();
            } catch(JSONException | IOException e) {
                e.printStackTrace();
                Log.e("WEATHER_UPDATE_ERR", Objects.requireNonNull(e.getMessage()));
            }
        }
    }

    /** Writes timestamp, current & saved location weather profiles to shared preferences. */
    private void saveToSharedPrefs() {
        SharedPreferences prefs = getApplication().getSharedPreferences(getApplication().getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        Gson gson = new Gson();

        prefs.edit().putString(getApplication().getString(R.string.keys_prefs_weathervm_current), gson.toJson(getCurrentLocationWeatherProfile().getValue())).apply();
        prefs.edit().putString(getApplication().getString(R.string.keys_prefs_weathervm_saved), gson.toJson(getSavedLocationWeatherProfiles().getValue())).apply();
        prefs.edit().putLong(getApplication().getString(R.string.keys_prefs_weathervm_lastupdated), mLastUpdated).apply();

    }

    /** Static factory class for building view model. */
    public static class WeatherFactory extends ViewModelProvider.NewInstanceFactory {

        /** The application instance. */
        private final Application mApplication;

        /**
         * Constructor
         * @param theApplication application instance.
         */
        public WeatherFactory(Application theApplication) {
            mApplication = theApplication;
        }

        /**
         * {@inheritDoc}
         * If creating new instance, checks shared preferences for
         * weather profiles first before creating new instance.
         */
        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public WeatherProfileViewModel create(@NonNull Class modelClass) {
            if(mInstance == null) {
                mInstance = new WeatherProfileViewModel(mApplication);
                SharedPreferences prefs =  mApplication.getSharedPreferences(mApplication.getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
                if(prefs.contains(mApplication.getString(R.string.keys_prefs_weathervm_current))
                        &&prefs.contains(mApplication.getString(R.string.keys_prefs_weathervm_saved))
                        &&prefs.contains(mApplication.getString(R.string.keys_prefs_weathervm_lastupdated))) {

                    //setup Gson and types
                    Gson gson = new Gson();
                    Type typeCurrent = new TypeToken<WeatherProfile>(){}.getType();
                    Type typeSaved = new TypeToken<List<WeatherProfile>>(){}.getType();

                    // Get current WP, saved location WPs & last updated from SharedPrefs and convert using Gson
                    String currentWPasString = prefs.getString(mApplication.getString(R.string.keys_prefs_weathervm_current), "");
                    WeatherProfile currentWP = gson.fromJson(currentWPasString, typeCurrent);

                    String savedWPsAsString = prefs.getString(mApplication.getString(R.string.keys_prefs_weathervm_saved), "");
                    ArrayList<WeatherProfile> savedWPs = gson.fromJson(savedWPsAsString, typeSaved);

                    long lastUpdated = prefs.getLong(mApplication.getString(R.string.keys_prefs_weathervm_lastupdated), 0);

                    // Set new instance's fields to what we pulled from SharedPrefs
                    mInstance.setCurrentLocationWeatherProfile(currentWP);
                    mInstance.setSavedLocationWeatherProfile(savedWPs);
                    mInstance.setTimeStamp(lastUpdated);
                }
            }
            return mInstance;
        }
    }
}