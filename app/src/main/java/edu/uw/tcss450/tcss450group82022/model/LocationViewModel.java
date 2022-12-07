package edu.uw.tcss450.tcss450group82022.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/** ViewModel to keep track of device location throughout lifecycle of app */
public class LocationViewModel extends ViewModel {

    /**
     * This ViewModel will be a singleton, meaning there will be only one instantiation and this
     * static variable will reference it. To implement this design pattern, we need a factory
     * method. See getFactory()
     */
    private static LocationViewModel mInstance;

    /**
     * Stores the Location object wrapped in a MutableLiveData. MutableLiveData implements a flavor
     * of the Observer Design Pattern. We will add Observers to this state in the fragments.
     */
    private MutableLiveData<Location> currentLocation;


    /**
     * Private to limit instantiation to this class and this class only. To obtain one (the only
     * one) of these objects, users must call the factory method.
     */
    private LocationViewModel() {
        currentLocation = new MutableLiveData<>();
    }


    /**
     * Obtain access to the LiveData object. This is usually done to allow the addition and removal
     * of Observers. Note the Polymorphic return type.
     * @return the LaveData to observe
     */
    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }


    /**
     * Change the Location state of this ViewModel.
     * @param location the new Location
     */
    public void changeLocation(final Location location) {
        currentLocation.setValue(location);
    }


    /**
     * Factory method. This ViewModel is a singleton. Use this factory method to obtain a
     * ViewModelProvider.Factory that can then be used to obtain the ViewModel instance.
     * @return a factory to provide instances of this ViewModel
     */
    public static ViewModelProvider.Factory getFactory() {
        return new ViewModelProvider.Factory() {

            /** {@inheritDoc} */
            @SuppressWarnings("unchecked")
            @NonNull
            @Override
            public LocationViewModel create(@NonNull Class modelClass) {
                if (mInstance == null) {
                    mInstance = new LocationViewModel();
                }
                return mInstance;
            }
        };
    }
}