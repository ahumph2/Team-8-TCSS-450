package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.tcss450group82022.databinding.ZipcodeDialogBinding;

public class ZipcodeDialog extends DialogFragment {

    private ZipcodeDialogBinding binding;
    private ZipcodeViewModel mZipModel;
    //private FiveDayHomeViewModel mFiveModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mZipModel = new ViewModelProvider(getActivity()).get(ZipcodeViewModel.class);
        //mFiveModel = new ViewModelProvider(getActivity()).get(FiveDayHomeViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZipModel.connect(binding.editTextEnterZip.getText().toString());
                Log.d("BUTTON","clicked");
                dismiss();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ZipcodeDialogBinding.inflate(inflater, container, false);

        return  binding.getRoot();
    }
}
