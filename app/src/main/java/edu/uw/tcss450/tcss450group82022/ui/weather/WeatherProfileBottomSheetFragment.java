package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfile;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfileViewModel;

/**  */
public class WeatherProfileBottomSheetFragment extends BottomSheetDialogFragment {
    private WeatherProfileViewModel mModel;
    private WeatherProfile mProfile;

    public WeatherProfileBottomSheetFragment() { /* Required empty public constructor */ }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_weather_profile_bottom_sheet, container, false);

        WeatherProfileBottomSheetFragmentArgs args = WeatherProfileBottomSheetFragmentArgs.fromBundle(requireArguments());
        mProfile = args.getWeatherProfile();
        mModel = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) new WeatherProfileViewModel.WeatherFactory(requireActivity().getApplication()))
                        .get(WeatherProfileViewModel.class);
        if(Objects.requireNonNull(mModel.getCurrentLocationWeatherProfile().getValue()).equals(mProfile)) {
            root.findViewById(R.id.btn_profilemenu_remove).setVisibility(View.GONE);
            root.findViewById(R.id.btn_profilemenu_use).setOnClickListener(tView -> useProfile());
        } else {
            root.findViewById(R.id.btn_profilemenu_use).setOnClickListener(tView -> useProfile());
            root.findViewById(R.id.btn_profilemenu_remove).setOnClickListener(tView -> removeProfile());
        }
        return root;
    }

    /**
     * Removes the selected location from the list of stored locations.
     */
    private void removeProfile() {
        //get index of WP to remove in list and remove it
        List<WeatherProfile> list = mModel.getSavedLocationWeatherProfiles().getValue();
        int index = Objects.requireNonNull(list).indexOf(mProfile);
        mModel.removeLocation(index);

        //Set WP to display as current if selected location was removed
        if(mModel.getSelectedLocationWeatherProfile().getValue() == null
                || Objects.requireNonNull(mModel.getSelectedLocationWeatherProfile().getValue()).equals(mProfile)) {
            mModel.setSelectedLocationWeatherProfile(mModel.getCurrentLocationWeatherProfile().getValue());
        }

        // Close bottom menu
        dismiss();
    }

    /**
     * Confirms using the selected location as the one for other purposes.
     */
    private void useProfile() {
        mModel.setSelectedLocationWeatherProfile(mProfile);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_global_nav_weather);

        dismiss();
    }
}
