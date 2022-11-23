package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentFiveDayListBinding;

/**
 * A simple {@link Fragment} subclass.

 */
public class FiveDayListFragment extends Fragment {

    private FiveDayViewModel mModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(FiveDayViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_five_day_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FragmentFiveDayListBinding binding = FragmentFiveDayListBinding.bind(getView());

        mModel.addResponseObserver(getViewLifecycleOwner(), dayList ->{

                binding.listRoot.setAdapter(
                        new DayRecyclerViewAdapter(dayList));

        });




    }
}