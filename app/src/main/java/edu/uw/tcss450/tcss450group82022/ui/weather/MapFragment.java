package edu.uw.tcss450.tcss450group82022.ui.weather;


import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfile;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfileViewModel;
import edu.uw.tcss450.tcss450group82022.utils.GetAsyncTask;
import edu.uw.tcss450.tcss450group82022.utils.Utils;

/** Handles logic for map view. */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    /** The Google Maps object to use for the fragment's interactions. */
    private GoogleMap mMap;
    /** A Google Maps API Geocoder object to use with locations. */
    private Geocoder mCoder;
    /** The marker to display the selected location. */
    private Marker mMarker;
    /** The WeatherProfileViewModel to update with information from the map. */
    private WeatherProfileViewModel mWeatherVM;

    /** Required empty public constructor. */
    public MapFragment() {}

    /** {@inheritDoc} */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * {@inheritDoc}
     * Setup instance fields and set onClickListeners.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCoder = new Geocoder(requireActivity().getApplicationContext());
        mWeatherVM = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new WeatherProfileViewModel.WeatherFactory(requireActivity().getApplication()))
                .get(WeatherProfileViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        view.findViewById(R.id.btn_map_use).setOnClickListener(v -> returnLocation());

    }

    /**
     * {@inheritDoc}
     * Add marker to user selected location if set, otherwise the device location.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in the selected location from before if available; otherwise current device location and move the camera
        WeatherProfile previouslySelected = mWeatherVM.getSelectedLocationWeatherProfile().getValue();
        LatLng current = previouslySelected == null
                ? Objects.requireNonNull(mWeatherVM.getCurrentLocationWeatherProfile().getValue()).getLocation()
                : previouslySelected.getLocation();
        mMarker = mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(this);
    }

    /**
     * {@inheritDoc}
     * Move move marker and update map.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        mMarker.remove();
        try { // Attempt to use the location's address as a label via Google Maps' reverse Geocoding
            List<Address> place = mCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(place.get(0).getAddressLine(0)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        } catch (IOException e) {
            e.printStackTrace();
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

    /** Returns the currently selected location as the one to be used in WeatherFragment. */
    private void returnLocation() {
        LatLng mapLocation = mMarker.getPosition();
        WeatherProfile wpToLoad = null;
        WeatherProfileViewModel weatherVM = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new WeatherProfileViewModel.WeatherFactory(requireActivity().getApplication()))
                .get(WeatherProfileViewModel.class);

        Log.i("MAP", mapLocation.toString());

        // Get location info from LatLng passed back
        String locationStr = "";
        try {
            Address address = Utils.getAddressFromLocation(mapLocation.latitude, mapLocation.longitude, getContext());
            String area = address.getLocality();
            String state = address.getAdminArea();
            locationStr = Utils.getFormattedLocation(address);
        } catch (IOException e) {e.printStackTrace();}

        // Compare to saved locations
        if(!"".equals(locationStr)) {
            for(WeatherProfile wp : Objects.requireNonNull(weatherVM.getSavedLocationWeatherProfiles().getValue())) {
                if(locationStr.equals(wp.getLocationStr())) {
                    wpToLoad = wp;
                    break;
                }
            }
        }

        // Need to get weather info from API
        if(wpToLoad == null) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority("tcss450-2022au-group8.herokuapp.com")
                    .appendPath("weather")
                    .appendPath(mapLocation.latitude + ":" + mapLocation.longitude)
                    .build();

            Log.d("API_CALL_MAP", uri.toString());

            new GetAsyncTask.Builder(uri.toString())
                    .onPostExecute(this::fetchWeatherPost)
                    .onCancelled(error -> Log.e("", error))
                    .build().execute();
        }
    }

    /**
     * Parses JSON for information to load in weather fragment.
     * @param result the JSON response from weather API
     */
    private void fetchWeatherPost(final String result) {
        WeatherProfile wpToLoad = null;
        try {
            JSONObject root = new JSONObject(result);

            String currJSONStr = root.getJSONObject("current").toString();
            String dailyJSONStr = root.getJSONArray("daily").toString();
            String hourlyJSONStr = root.getJSONArray("hourly").toString();

            Address addr = Utils.getAddressFromLocation(root.getDouble("lat"),
                    root.getDouble("lon"),
                    getContext());
            String locationStr = Utils.getFormattedLocation(addr);

            wpToLoad = new WeatherProfile(mMarker.getPosition(), currJSONStr, dailyJSONStr, hourlyJSONStr, locationStr);

            // Set current location to one chosen on map so it's loaded again when they go back to map
            WeatherProfileViewModel weatherVm = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new WeatherProfileViewModel.WeatherFactory(requireActivity().getApplication()))
                    .get(WeatherProfileViewModel.class);
            weatherVm.setSelectedLocationWeatherProfile(wpToLoad);
        } catch(JSONException | IOException e) {
            e.printStackTrace();
            Log.e("WEATHER_UPDATE_ERR", Objects.requireNonNull(e.getMessage()));
        }

        if(wpToLoad == null) {Toast.makeText(getContext(), "Oops, something went wrong. Please try again.", Toast.LENGTH_LONG).show();}

        Navigation.findNavController(requireView()).navigate(R.id.action_global_nav_weather);
    }
}