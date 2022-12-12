package edu.uw.tcss450.tcss450group82022.ui.contacts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatCard;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatListFragment;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatListFragmentDirections;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactListRecyclerViewAdapter} factory method to
 * create an instance of this fragment.
 */
public class ContactListRecyclerViewAdapter extends RecyclerView.Adapter<ContactListRecyclerViewAdapter.ContactViewHolder> {
    // Store all of the chat cards to present
    private final List<ContactCard> mContactCards;
    private FragmentContactCardBinding binding;
    private final ContactListViewModel mModel;
    private final UserInfoViewModel mUserModel;
    private final Fragment mContactListFragment;

    public ContactListRecyclerViewAdapter(List<ContactCard> items, @NonNull ContactListFragment fragment){
        this.mContactCards = items;
        mModel = new ViewModelProvider(fragment.requireActivity()).get(ContactListViewModel.class);
        ViewModelProvider provider = new ViewModelProvider(fragment.requireActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactListFragment = fragment;
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
            binding.textPendingContacts.setVisibility(View.INVISIBLE);
            binding.buttonAcceptContact.setVisibility(View.INVISIBLE);
            if(!contactCard.getPending()){
                binding.buttonEnterContact.setOnClickListener(view -> {
                    //TODO add navigation later step
                    Navigation.findNavController(mView).navigate(
                            ContactListFragmentDirections
                                    .actionNavigationContactListToNavigationContact(contactCard));
                });
                binding.contactName.setText(contactCard.getContactFullName());
                binding.buttonDeclineContact.setVisibility(View.VISIBLE);
            }
            else{
                binding.contactName.setText(contactCard.getmContactEmail());
                binding.textPendingContacts.setVisibility(View.VISIBLE);
                if(contactCard.getIsMemberIdA()){
                    binding.buttonAcceptContact.setVisibility(View.VISIBLE);
                    // Accept functionality
                    binding.buttonAcceptContact.setOnClickListener(button -> {
                        Log.e("CONTACTS", "Clicked accept contact");
                        mModel.updateContact(mUserModel.getmJwt(), mUserModel.getEmail(), String.valueOf(contactCard.getMemberID()));
                        mModel.addPutResponseObserver(mContactListFragment.getViewLifecycleOwner(), this::observePutResponse);
                        binding.buttonAcceptContact.setVisibility(View.INVISIBLE);
                        binding.textPendingContacts.setVisibility(View.INVISIBLE);
                    });

                }
            }
            // Decline functionality
            binding.buttonDeclineContact.setVisibility(View.VISIBLE);
            binding.buttonDeclineContact.setOnClickListener(button -> {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mView.getContext());
                dialogBuilder.setTitle("Confirm");
                dialogBuilder.setMessage("Are you sure you want to delete this contact?");
                dialogBuilder.setPositiveButton("YES", (dialog, which) -> {
                    // Delete the chatroom
                    mModel.deleteContact(mUserModel.getmJwt(), mUserModel.getEmail(), String.valueOf(contactCard.getMemberID()));
                    mModel.addDeleteResponseObserver(mContactListFragment.getViewLifecycleOwner(), this::observePutResponse);
                    mModel.getContacts(mUserModel.getmJwt(), mUserModel.getEmail());
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
        private void observePutResponse(final JSONObject response) {
            Log.e("CONTACTS", "Response for delete: " + response);
            if (response.length() > 0) {
                if (response.has("success")) {
                    Log.e("CONTACTS", "Passed initial check");
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
    }
}