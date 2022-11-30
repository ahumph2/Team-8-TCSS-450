package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentTwelveHourListBinding;

/**

 * create an instance of this fragment.
 */
public class TwelveHourListFragment extends Fragment {

    private TwelveHourViewModel mModel;
    private List<HourPost> hourPosts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            mModel = new ViewModelProvider(getActivity()).get(TwelveHourViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_twelve_hour_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FragmentTwelveHourListBinding binding = FragmentTwelveHourListBinding.bind(getView());


        mModel.addResponseObserver(getViewLifecycleOwner(), hourList ->{
            binding.layoutRoot.setAdapter(
                    new HourRecyclerViewAdapter(hourList));
        });




    }
}