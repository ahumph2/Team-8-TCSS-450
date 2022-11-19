package edu.uw.tcss450.tcss450group82022.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentSecondHomeBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondHomeFragment extends Fragment {


    public SecondHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentSecondHomeBinding binding = FragmentSecondHomeBinding.bind(requireView());
        //Note argument sent to the ViewModelProvider constructor. It is the Activity that
        //holds this fragment.
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        binding.textEmail.setText(getString(R.string.label_second_home, model.getEmail()));

        //On button click, navigate to Third Home
        binding.buttonNext.setOnClickListener(button ->
                Navigation.findNavController(requireView()).navigate(
                        SecondHomeFragmentDirections
                                .actionSecondHomeFragmentToThirdHomeFragment())
        );
    }
}
