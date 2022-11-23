package edu.uw.tcss450.uiandnavigationlab.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.uiandnavigationlab.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayPostFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_post, container, false);
    }
}