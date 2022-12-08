package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.tcss450group82022.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;
import edu.uw.tcss450.tcss450group82022.ui.contacts.ContactCard;
import edu.uw.tcss450.tcss450group82022.ui.contacts.ContactListFragmentDirections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserListRecyclerViewAdapter} factory method to
 * create an instance of this fragment.
 */
public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<UserListRecyclerViewAdapter.UserViewHolder> {
// Store all of the chat cards to present
    private final List<ContactCard> mUserCards;

    public UserListRecyclerViewAdapter(List<ContactCard> items){
        this.mUserCards = items;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setContactCard(mUserCards.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserCards.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Blog Recycler View.
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactCardBinding binding;

        public UserViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
        }

        void setContactCard(final ContactCard contactCard) {
            binding.buttonEnterContact.setOnClickListener(view -> {
                //TODO add navigation later step
                Navigation.findNavController(mView).navigate(
                        ContactListFragmentDirections
                                .actionNavigationContactListToNavigationContact(contactCard));
            });
            binding.contactName.setText(contactCard.getmContactEmail());
            /*
            binding.buttonDeleteChatroom.setOnClickListener(view ->{
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mView.getContext());
                dialogBuilder.setTitle("Confirm");
                dialogBuilder.setMessage("Are you sure you want to delete " + contactCard.getChatName() + "?");
                dialogBuilder.setPositiveButton("YES", (dialog, which) -> {
                    // Delete the chatroom
                    mModel.connectDelete(mUserModel.getmJwt(), contactCard.getChatId());
                    mModel.addDeleteResponseObserver(mChatListFragment.getViewLifecycleOwner(), this::observeDeleteResponse);
                    dialog.dismiss();
                });

                dialogBuilder.setNegativeButton("NO", (dialog, which) -> {
                    // close the dialog
                    dialog.dismiss();
                });
                AlertDialog alert = dialogBuilder.create();
                alert.show();
            });*/
        }

        /**
         * An observer on the HTTP Response from the web server.
         *
         * @param response the Response from the server
         */
        /*private void observeDeleteResponse(final JSONObject response) {
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
        }*/
    }
}