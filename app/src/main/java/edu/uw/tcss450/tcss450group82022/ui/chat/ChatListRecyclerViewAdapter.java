package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatCardBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatListRecyclerViewAdapter} factory method to
 * create an instance of this fragment.
 */
public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatViewHolder> {
// Store all of the chat cards to present
    private final List<ChatCard> mChatCards;

    public ChatListRecyclerViewAdapter(List<ChatCard> items){
        this.mChatCards = items;
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
        private ChatCard mChatCard;
        public ChatViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        void setChatCard(final ChatCard chatCard) {
            mChatCard = chatCard;
            binding.buttonEnterChat.setOnClickListener(view -> {
                //TODO add navigation later step
                Navigation.findNavController(mView).navigate(
                        ChatListFragmentDirections
                                .actionNavigationChatsToChatFragment(chatCard));
            });
            binding.textChatId.setText(chatCard.getChatName());
        }
    }
}