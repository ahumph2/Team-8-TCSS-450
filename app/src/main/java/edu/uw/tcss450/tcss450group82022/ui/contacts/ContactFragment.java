package edu.uw.tcss450.tcss450group82022.ui.contacts;

import static edu.uw.tcss450.tcss450group82022.ui.contacts.ContactFragmentDirections.actionNavigationContactToNavigationChats;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactListBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatCard;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatListFragmentDirections;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatListViewModel;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


    private ContactViewModel mContactModel;
    private UserInfoViewModel mUserModel;
    private ContactFragmentArgs mArgs;
    private ContactListRecyclerViewAdapter contactListAdapter;
    private FragmentContactListBinding mBinding;
    private ChatListViewModel mModel;//to be able to navigate and create new chats from contacts
    private ChatViewModel mChatModel;
    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactModel = provider.get(ContactViewModel.class);
        mArgs = ContactFragmentArgs.fromBundle(getArguments());
        mChatModel = provider.get(ChatViewModel.class);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentContactBinding binding = FragmentContactBinding.bind(getView());

        binding.contactNameEdittext.setText(mArgs.getContact().getContactFullName());
        binding.contactEmailEdittext.setText(mArgs.getContact().getmContactEmail());
        //On button click, navigate to Third Home
        /*binding.contactChatButton.setOnClickListener(button ->
                Navigation.findNavController(requireView()).navigate(
                        actionNavigationContactToNavigationChats()
                        ));*/
        binding.contactChatButton.setOnClickListener(button -> {
            // Create a new popup dialog. It handles the name of the new chat
            // as well as submitting the request
            final View newChatPopupView = getLayoutInflater().inflate(R.layout.new_chat_popup_window, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
            dialogBuilder.setView(newChatPopupView);
            Dialog dialog = dialogBuilder.create();
            dialog.show();
            // Find the submit button for the dialog
            Button submitButton = newChatPopupView.findViewById(R.id.new_chat_submit_button);

            // When the submit button is clicked
            submitButton.setOnClickListener(innerButton -> {
                //  Make spinner appear
                // Using the text specified by user, call a post to chats with the new name
                EditText nameText = newChatPopupView.findViewById(R.id.new_chat_name);
                mModel.connectPost(mUserModel.getmJwt(), String.valueOf(nameText.getText()));
                // Observe the response we get from the POST call
                mModel.addPostResponseObserver(getViewLifecycleOwner(), response -> {
                    observePostResponse(response, dialog, nameText);
                });
            });
        });
    }

    private void observePostResponse(final JSONObject response, Dialog dialog, EditText nameText){
        if (response.length() > 0) {
            if (response.has("chatID")) {
                try {
                    String chatId = response.getString("chatID");
                    // Close the popup and get rid of spinner
                    dialog.dismiss();
                    Log.i("CHATID", "ChatId: " + chatId);
                    // Prevent multiple put calls / add user to new chat
                    if(!Objects.equals(chatId, "")){
                        mModel.connectPut(mUserModel.getmJwt(),chatId);
                    }
                    // Observe response from put to ensure we have success
                    mModel.addPutResponseObserver(getViewLifecycleOwner(), this::observePutResponse);

                    //try here
                    mChatModel.connectPostAddUser(mUserModel.getmJwt(), chatId, mArgs.getContact().getmContactEmail());
                    mChatModel.addPostResponseObserver(getViewLifecycleOwner(), response2 ->{
                        observeAddUserResponse(response2, dialog, nameText,chatId, nameText);
                    });

                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else if (response.has("code")){
                try {
                    nameText.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else
                Log.d("JSON Response", "No Response");
        }
    }
    /**
     * An observer on the HTTP Response from the web server.
     *
     * @param response the Response from the server
     */
    private void observePutResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("success")) {
                try {
                    if (response.get("success").equals(true)) {
                        Log.i("SUCCESS", "Success!");

                    } else
                        Log.e("PUT Response", "Put response failed");
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else
                Log.d("JSON Response", "No Response");
        }
    }

    private void observeAddUserResponse(final JSONObject response, Dialog dialog, EditText emailText, String chatid, EditText nameText) {
        if (response.length() > 0) {
            if (response.has("success")) {
                try {
                    if (response.get("success").equals(true)) {
                        Log.i("SUCCESS", "Success!");
                        dialog.dismiss();
                        ChatCard tempCard = new ChatCard.Builder(chatid,nameText.getText().toString()).build();
                        //TODO add navigation later step
                        Navigation.findNavController(getView()).navigate(
                                ContactFragmentDirections
                                        .actionNavigationContactToChatFragment(tempCard));
                        mModel.connectGet(mUserModel.getmJwt(), mUserModel.getEmail());
                        mChatModel.connectGetUserList(mUserModel.getmJwt(),chatid);
                    } else
                        Log.e("POST Response", "Post response failed");
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else if (response.has("code")){
                try {
                    emailText.setError(
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
