package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentLocationBinding;

/**
 * A simple {@link Fragment} subclass.

 */
public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private LocationViewModel mModel;
    private GeopositionViewModel mGeoModel;
    private CurrentWeatherViewModel mWeatherModel;
    private FiveDayHomeViewModel mFiveModel;
    private TwelveHourHomeViewModel mTwelveModel;
    private TwelveHourViewModel mTwelveListModel;
    private FiveDayViewModel mFiveListModel;

    private GoogleMap mMap;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentLocationBinding binding = FragmentLocationBinding.bind(getView());

        mModel = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        mGeoModel = new ViewModelProvider(getActivity()).get(GeopositionViewModel.class);
        mFiveModel = new ViewModelProvider(getActivity()).get(FiveDayHomeViewModel.class);
        mTwelveModel = new ViewModelProvider(getActivity()).get(TwelveHourHomeViewModel.class);
        mTwelveListModel = new ViewModelProvider(getActivity()).get(TwelveHourViewModel.class);
        mFiveListModel = new ViewModelProvider(getActivity()).get(FiveDayViewModel.class);
        mModel.addLocationObserver(getViewLifecycleOwner(), location ->
                binding.textLatLong.setText(location.toString()));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mModel.addLocationObserver(getViewLifecycleOwner(), location -> {
            if(location != null) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMyLocationEnabled(true);

                final LatLng c = new LatLng(location.getLatitude(), location.getLongitude());
                //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
            }
        });

        mMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());

        String lat = String.valueOf(latLng.latitude);
        String lng = String.valueOf(latLng.longitude);

        FragmentLocationBinding binding = FragmentLocationBinding.bind(getView());
        binding.textLatLong.setText("Latitude: " + lat + ", Longitude: " +lng );

        binding.button.setOnClickListener(v-> {
            mModel.addLocationObserver(getViewLifecycleOwner(), response ->
                    mGeoModel.connect(lat, lng));
            mGeoModel.addResponseObserver(getViewLifecycleOwner(),response ->{
                try {
                    mFiveModel.connect(response.getString("Key"));
                    mTwelveModel.connect(response.getString("Key"));
                    mTwelveListModel.connect(response.getString("Key"));
                    mFiveListModel.connect(response.getString("Key"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
                     // TODO: dismiss fragment

        });

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
    }
}