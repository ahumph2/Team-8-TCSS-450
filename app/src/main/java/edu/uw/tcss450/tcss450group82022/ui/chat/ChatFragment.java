package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatBinding;
import edu.uw.tcss450.tcss450group82022.model.NewMessageCountViewModel;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;
import edu.uw.tcss450.tcss450group82022.ui.contacts.ContactListRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    //The chat ID for "global" chat
    //private static final int HARD_CODED_CHAT_ID = 1;

    private ChatFragmentArgs mArgs;
    private int mChatId;

    private ChatViewModel mChatModel;
    private UserInfoViewModel mUserModel;

    private ChatSendViewModel mSendModel;

    private NewMessageCountViewModel mNewMessageModel;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatViewModel.class);
        mArgs = ChatFragmentArgs.fromBundle(getArguments());
        mChatId = Integer.parseInt(mArgs.getChat().getChatId());
        mChatModel.getFirstMessages(mChatId, mUserModel.getmJwt());

        mSendModel = provider.get(ChatSendViewModel.class);

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentChatBinding binding = FragmentChatBinding.bind(getView());
        binding.textChatName.setText(mArgs.getChat().getChatName());

        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerMessages;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
        rv.setAdapter(new ChatRecyclerViewAdapter(
                mChatModel.getMessageListByChatId(mChatId),
                mUserModel.getEmail()));


        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(mChatId, mUserModel.getmJwt());
        });

        mChatModel.addMessageObserver(mChatId, getViewLifecycleOwner(),
                list -> {
                    /*
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });

        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(mChatId,
                    mUserModel.getmJwt(),
                    binding.editMessage.getText().toString());
        });
        //when we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->{
            binding.editMessage.setText("");
            mNewMessageModel.reset();
        });

        // Clicking the button to add a user to the chat room
        binding.buttonAddNewUserChats.setOnClickListener(button -> {
            // Create the add user popup
            final View newUserPopupView = getLayoutInflater().inflate(R.layout.popup_new_user_chats, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
            dialogBuilder.setView(newUserPopupView);
            Dialog dialog = dialogBuilder.create();
            dialog.show();
            // Grabbing views
            Button submitButton = newUserPopupView.findViewById(R.id.button_submit_new_user_chats);
            TextView chatNameView = newUserPopupView.findViewById(R.id.text_chat_room_name);
            RecyclerView chatUsersRecyclerView = newUserPopupView.findViewById(R.id.recycler_view_chatroom_users);

            // Set chat room name
            chatNameView.setText(mArgs.getChat().getChatName());
            // Get list of users in the room
            mChatModel.connectGetUserList(mUserModel.getmJwt(), mArgs.getChat().getChatId());
            mChatModel.addUserListObserver(getViewLifecycleOwner(), userList -> {
                if (!userList.isEmpty()) {
                    Log.e("USERS", "User list is not empty. It is: " + userList);
                    chatUsersRecyclerView.setAdapter(
                            new UserListRecyclerViewAdapter(userList)
                    );
                }
            });
            // Add submit button functionality
            submitButton.setOnClickListener(innerButton -> {
                EditText emailText = newUserPopupView.findViewById(R.id.text_add_new_chat_email);
                mChatModel.connectPostAddUser(mUserModel.getmJwt(), mArgs.getChat().getChatId(), emailText.getText().toString());
                mChatModel.addPostResponseObserver(getViewLifecycleOwner(), response ->{
                    observeAddUserResponse(response, dialog, emailText);
                });
            });
        });
    }
    /**
     * An observer on the HTTP Response from the web server.
     *
     * @param response the Response from the server
     */
    private void observeAddUserResponse(final JSONObject response, Dialog dialog, EditText emailText) {
        if (response.length() > 0) {
            if (response.has("success")) {
                try {
                    if (response.get("success").equals(true)) {
                        Log.i("SUCCESS", "Success!");
                        dialog.dismiss();
                        mSendModel.sendMessage(mChatId,
                                mUserModel.getmJwt(),
                                "Welcome to the chat!");
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
