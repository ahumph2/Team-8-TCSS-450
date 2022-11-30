package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.tcss450group82022.R;

/**

 * create an instance of this fragment.
 */
public class HourPostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hour_post, container, false);
    }
}