package edu.uw.tcss450.tcss450group82022.ui.contacts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatCard;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatListFragmentDirections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactListRecyclerViewAdapter} factory method to
 * create an instance of this fragment.
 */
public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ContactViewHolder> {
    // Store all of the chat cards to present
    private final List<ContactCard> mContactCards;
    private FragmentContactCardBinding binding;

    public ContactListRecyclerViewAdapter(List<ContactCard> items){
        this.mContactCards = items;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContactCard(mContactCards.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactCards.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Blog Recycler View.
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final FragmentContactCardBinding binding;

        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
        }

        public void setContactCard(final ContactCard contactCard){
            binding.buttonEnterContact.setOnClickListener(view -> {
                //TODO add navigation later step
                Navigation.findNavController(mView).navigate(
                        ContactListFragmentDirections
                                .actionNavigationContactListToNavigationContact(contactCard));
            });
            binding.contactName.setText(contactCard.getContactFullName());
        }
    }
}