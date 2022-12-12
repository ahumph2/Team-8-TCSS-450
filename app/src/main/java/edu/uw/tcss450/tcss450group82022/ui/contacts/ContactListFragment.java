package edu.uw.tcss450.tcss450group82022.ui.contacts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatListBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactListBinding;
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
        mModel.getContacts(mUserModel.getmJwt(), mUserModel.getEmail());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentContactListBinding binding = FragmentContactListBinding.bind(getView());

        binding.contactAddNew.setOnClickListener(button -> {
            final View newContactPopupView = getLayoutInflater().inflate(R.layout.popup_add_new_contact, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
            dialogBuilder.setView(newContactPopupView);
            Dialog dialog = dialogBuilder.create();
            dialog.show();
            // Find the submit button for the dialog
            Button submitButton = newContactPopupView.findViewById(R.id.new_contact_submit_button);

            // When the submit button is clicked
            submitButton.setOnClickListener(innerButton -> {
                //  Make spinner appear
                binding.layoutWait.setVisibility(View.VISIBLE);
                // Using the text specified by user, call a post to chats with the new name
                EditText contactEmailText = newContactPopupView.findViewById(R.id.new_contact_name);
                mModel.addContact(mUserModel.getmJwt(),mUserModel.getEmail(),String.valueOf(contactEmailText.getText()));
                // Observe the response we get from the POST call
                mModel.addPostResponseObserver(getViewLifecycleOwner(), response -> {
                    binding.layoutWait.setVisibility(View.GONE);
                    observePostResponse(response, dialog, contactEmailText);
                });
            });
        });

        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            binding.layoutWait.setVisibility(View.GONE);
            if (!contactList.isEmpty()) {
                binding.listRoot.setAdapter(
                        new ContactListRecyclerViewAdapter(contactList, this)
                );
            }
        });
    }

    private void observePostResponse(final JSONObject response, Dialog dialog, EditText contactText){
        if (response.length() > 0) {
            if (response.has("success")) {
                try {
                    if (response.get("success").equals(true)) {
                        Log.i("SUCCESS", "Success!");
                        dialog.dismiss();
                        mModel.getContacts(mUserModel.getmJwt(),mUserModel.getEmail());
                    } else
                        Log.e("POST Response", "Post response failed");
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else if (response.has("code")){
                try {
                    contactText.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else
                Log.d("JSON Response", "No Response");
        }
    }
}