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
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactCardBinding;

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
        ContactCard modal = mContactCards.get(position);

        holder.contactName.setText(modal.getContactFullName());
        /**
        holder.contactDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(
                        ContactListFragmentDirections.actionNavigationContactListToNavigationContact(
                                modal.getContactFullName(), modal.getmContactEmail())
                );
            }
        });
         */
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
        private final TextView contactName;
        private ImageButton contactDelete;

        public ContactViewHolder(View view) {
            super(view);
            contactName = view.findViewById(R.id.contact_name);
            contactDelete = view.findViewById(R.id.contact_delete);
        }
    }
}