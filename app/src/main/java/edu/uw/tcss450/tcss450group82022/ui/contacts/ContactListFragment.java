package edu.uw.tcss450.tcss450group82022.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatListBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactListFragment} factory method to
 * create an instance of this fragment.
 */
public class ContactListFragment extends Fragment {

    private ContactListViewModel mModel;
    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ContactListViewModel.class);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mModel.connectGet(mUserModel.getmJwt());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentChatListBinding binding = FragmentChatListBinding.bind(getView());

        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            Log.e("CONTACT", "contactList: " + contactList);
            if (!contactList.isEmpty()) {
                binding.listRoot.setAdapter(
                        new ContactListRecyclerViewAdapter(contactList)
                );
                binding.layoutWait.setVisibility(View.GONE);
            }
        });
    }
}