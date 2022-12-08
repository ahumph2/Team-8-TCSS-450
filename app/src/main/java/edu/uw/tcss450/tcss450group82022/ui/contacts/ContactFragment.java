package edu.uw.tcss450.tcss450group82022.ui.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactListBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatFragmentArgs;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


    private ContactViewModel mContactModel;
    private UserInfoViewModel mUserModel;
    private Bundle mArgs;
    private String mContactName;
    private ContactListRecyclerViewAdapter contactListAdapter;
    private FragmentContactListBinding mBinding;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactModel = provider.get(ContactViewModel.class);
        mContactModel.getFirst
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentContactListBinding.inflate(inflater);
        mArgs = getArguments();
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentContactBinding binding = FragmentContactBinding.bind(getView());

        binding.contactNameEdittext.setText(mContactName);
    }
}
