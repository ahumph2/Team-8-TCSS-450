package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.MainActivity;
import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatListBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListFragment#} factory method to
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {

    FragmentChatListBinding binding;
    private ChatListViewModel mModel;
    private UserInfoViewModel mUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mModel.connectGet(mUserModel.getmJwt(), mUserModel.getEmail());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentChatListBinding.bind(getView());
        binding.chatAddNew.setOnClickListener(button -> {
           final View newChatPopupView = getLayoutInflater().inflate(R.layout.new_chat_popup_window, null);
           AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());
           dialogBuilder.setView(newChatPopupView);
           Dialog dialog = dialogBuilder.create();
           dialog.show();
           Button submitButton = newChatPopupView.findViewById(R.id.new_chat_submit_button);

           submitButton.setOnClickListener(innerButton -> {
               binding.layoutWait.setVisibility(View.VISIBLE);
               EditText nameText = newChatPopupView.findViewById(R.id.new_chat_name);
               mModel.connectPost(mUserModel.getmJwt(), String.valueOf(nameText.getText()));
               mModel.addChatIdObserver(getViewLifecycleOwner(), chatId -> {
                   dialog.dismiss();
                   binding.layoutWait.setVisibility(View.GONE);
                   Log.i("CHATID", "ChatId: " + chatId);
                   if(!Objects.equals(chatId, "")){
                       mModel.connectPut(mUserModel.getmJwt(),chatId);
                   }
                   mModel.addPutResponseObserver(getViewLifecycleOwner(), this::observePutResponse);
               });
           });
        });
        mModel.addBlogListObserver(getViewLifecycleOwner(), chatList -> {
            Log.i("CHAT", "chatList: " + chatList);
            if (!chatList.isEmpty()) {
                binding.listRoot.setAdapter(
                        new ChatListRecyclerViewAdapter(chatList)
                );
            }
            binding.layoutWait.setVisibility(View.GONE);
        });
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
                        mModel.connectGet(mUserModel.getmJwt(), mUserModel.getEmail());
                    } else
                        Log.e("PUT Response", "Put response failed");
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else
                Log.d("JSON Response", "No Response");
        }
    }
}