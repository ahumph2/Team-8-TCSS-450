package edu.uw.tcss450.tcss450group82022.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.badge.BadgeDrawable;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentHomeBinding;
import edu.uw.tcss450.tcss450group82022.model.NewMessageCountViewModel;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private NewMessageCountViewModel mNewMessageModel;
    private Integer mMessageCount;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);
        mNewMessageModel.addMessageCountObserver(this, count -> {
            mMessageCount = count;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentHomeBinding binding = FragmentHomeBinding.bind(requireView());
        //Note argument sent to the ViewModelProvider constructor. It is the Activity that
        //holds this fragment.
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        binding.textEmail.setText(getString(R.string.label_home, model.getEmail()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date());
        binding.textTime.setText(getString(R.string.label_time, currentDateandTime));

        binding.textCurrentMessageCount.setText(getString(R.string.label_home_message_notification, String.valueOf(mMessageCount)));
        //On button click, navigate to Second Home
        /*
        binding.buttonNext.setOnClickListener(button ->
                Navigation.findNavController(requireView()).navigate(
                        HomeFragmentDirections
                                .actionHomeFragmentToSecondHomeFragment()));

         */
    }
}
