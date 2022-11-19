package edu.uw.tcss450.tcss450group82022.ui.incrementor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentIncrementorBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class IncrementorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_incrementor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //Gain access to the ViewBinding object as a local variable instead of instance var
        FragmentIncrementorBinding binding = FragmentIncrementorBinding.bind(getView());
        binding.textCount.setText("My Count is: 0");
        //Obtain access to the ViewModel. If this fragment object is new, the ViewModel
        //will be re/created. Note the parameter to the ViewModelProvider constructor - this.
        IncrementorViewModel model =
                new ViewModelProvider(getActivity()).get(IncrementorViewModel.class);
        //Add an observer the the MutableLiveData - mCount.
        model.addCountObserver(getViewLifecycleOwner(), count ->
                binding.textCount.setText("My Count is: " + model.getCount()));
        //On button click, increase the MutableLiveData - mCount
        binding.buttonIncrease.setOnClickListener(button -> model.increment());
    }
}