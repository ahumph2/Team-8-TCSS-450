package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListRecyclerViewAdapter} factory method to
 * create an instance of this fragment.
 */
public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatViewHolder> {
// Store all of the chat cards to present
    private final List<ChatCard> mChatCards;
    private final ChatListViewModel mModel;
    private final UserInfoViewModel mUserModel;
    private final Fragment mChatListFragment;

    public ChatListRecyclerViewAdapter(List<ChatCard> items, @NonNull ChatListFragment fragment){
        this.mChatCards = items;
        mModel = new ViewModelProvider(fragment.requireActivity()).get(ChatListViewModel.class);
        ViewModelProvider provider = new ViewModelProvider(fragment.requireActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatListFragment = fragment;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.setChatCard(mChatCards.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatCards.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Blog Recycler View.
     */
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatCardBinding binding;

        public ChatViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        void setChatCard(final ChatCard chatCard) {
            binding.buttonEnterChat.setOnClickListener(view -> {
                //TODO add navigation later step
                Navigation.findNavController(mView).navigate(
                        ChatListFragmentDirections
                                .actionNavigationChatsToChatFragment(chatCard));
            });
            binding.textChatId.setText(chatCard.getChatName());
            binding.buttonDeleteChatroom.setOnClickListener(view ->{
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mView.getContext());
                dialogBuilder.setTitle("Confirm");
                dialogBuilder.setMessage("Are you sure you want to delete " + chatCard.getChatName() + "?");
                dialogBuilder.setPositiveButton("YES", (dialog, which) -> {
                    // Delete the chatroom
                    mModel.connectDelete(mUserModel.getmJwt(), chatCard.getChatId());
                    mModel.addDeleteResponseObserver(mChatListFragment.getViewLifecycleOwner(), this::observeDeleteResponse);
                    dialog.dismiss();
                });

                dialogBuilder.setNegativeButton("NO", (dialog, which) -> {
                    // close the dialog
                    dialog.dismiss();
                });
                AlertDialog alert = dialogBuilder.create();
                alert.show();
            });
        }

        /**
         * An observer on the HTTP Response from the web server.
         *
         * @param response the Response from the server
         */
        private void observeDeleteResponse(final JSONObject response) {
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
}