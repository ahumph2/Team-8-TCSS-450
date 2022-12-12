package edu.uw.tcss450.tcss450group82022.ui.contacts;

import android.app.Application;
import android.text.Editable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.io.RequestQueueSingleton;

public class ContactListViewModel extends AndroidViewModel {
    private MutableLiveData<List<ContactCard>> mContactList;
    private MutableLiveData<JSONObject> mResponse;

    private MutableLiveData<JSONObject> mPutResponse;
    private MutableLiveData<JSONObject> mDeleteResponse;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>(new JSONObject());
        mPutResponse = new MutableLiveData<>(new JSONObject());
        mDeleteResponse = new MutableLiveData<>(new JSONObject());
    }
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<ContactCard>> observer) {
        mContactList.observe(owner, observer);
    }

    public void addPostResponseObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super JSONObject> observer){
        mResponse.observe(owner, observer);
    }

    public void addPutResponseObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super JSONObject> observer) {
        mPutResponse.observe(owner, observer);
    }

    public void addDeleteResponseObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super JSONObject> observer) {
        mDeleteResponse.observe(owner, observer);
    }
    /**
     * Gets the user's contact list
     * @return
     */
    private MutableLiveData<List<ContactCard>> getContactList(){
        return mContactList;
    }

    public List<ContactCard> getContactListValue(){
        return getContactList().getValue();
    }

    /**
     * A lot of stuff will happen here
     * We will be processing contacts
     * If we show up as the memberid B, that means we are the receiver.
     * If we show up as memberid A, we are the sender
     * If the contact is "verified", we simply create a contact card
     * with the memberid that isn't us.
     * If we are not verified, we do something different depending on if we are
     * Id A or B
     * @param response
     */
    private void handleGetContactsResult(final JSONObject response, final String jwt){
        JSONObject root = response;
        try{
            if(root.has("contacts")){
                Log.e("CONTACTS", "We have found the contacts root");
                JSONArray contactsArray = root.getJSONArray("contacts");
                // Grab our memberID
                String memberId = root.getString("memberId");
                Log.e("CONTACTS", "User memberId: " + memberId);
                for(int i = 0; i < contactsArray.length(); i++){
                    // A specific contact we are evaluating
                    JSONObject contact = contactsArray.getJSONObject(i);
                    Log.e("CONTACTS", "Contact object: " + contact.toString());
                    String memberIdA = contact.getString("memberid_a");
                    String memberIdB = contact.getString("memberid_b");
                    String verified = contact.getString("verified");
                    String chatCardMemberId = "";
                    boolean isMemberA = false;
                    boolean pending = false;

                    Log.e("CONTACTS", "MemberIdA: " + memberIdA + "\n" +
                            "MemberIdB: " + memberIdB + "\n" +
                            "Verified: " + verified);

                    // First decide which memberId gets a chat card

                    // We are memberIdA
                    if (memberIdA.equals(memberId)){
                        Log.e("CONTACTS", "We are memberid A, creating a contact for" +
                                "memberId B");
                        // Add a new contact card for memberIdB to the list
                        chatCardMemberId = memberIdB;
                    }
                    // We are memberIdB
                    else if (memberIdB.equals(memberId)){
                        Log.e("CONTACTS", "We are memberid B, creating a contact for" +
                                "memberId A");
                        chatCardMemberId = memberIdA;
                    }
                    // Get all detailed info for that member id and add that info
                    // to a chat card, add the card to our list

                    // Contact is not verified, no connection made
                    if(verified.equals("0")){
                        Log.e("CONTACTS", "Unverified contact");

                        pending = true;
                        if (memberIdB.equals(memberId)){
                            isMemberA = true;
                        }
                    }
                    getContactCardInfo(jwt, chatCardMemberId, pending, isMemberA);
                    Log.e("CONTACTS", "We should have updated our contactList: " + mContactList.getValue());
                }
            }

        }catch (JSONException e){
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }

    }

    /**
     * Add the contacts from JSONArray array to the mContactList
     * @param
     */
    private void handleResult(final JSONObject response, boolean pending, boolean isMemberIdA){
        mContactList.setValue(new ArrayList<>());
        JSONObject root = response;
        Log.e("CONTACTS", "Info Response: " + response.toString());
        try{
            if(root.has("contacts")){
                JSONArray contactsArray = root.getJSONArray("contacts");

                for(int i = 0; i < contactsArray.length(); i++){
                    JSONObject contact = contactsArray.getJSONObject(i);
                    ContactCard contactCard = new ContactCard(
                            contact.getString("firstname"),
                            contact.getString("lastname"),
                            contact.getString("email"),
                            contact.getInt("memberid"),
                            pending,
                            isMemberIdA
                    );
                    if (!mContactList.getValue().contains(contactCard)){
                        mContactList.getValue().add(contactCard);
                    } else
                        Log.wtf("Contact already exists", "Member ID already exists" + contactCard.getMemberID());
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
        mContactList.setValue(mContactList.getValue());
    }

    // Get all contacts relating to our memberid / email
    public void getContacts(final String jwt, final String email) {
        String url = getApplication().getResources().getString(R.string.base_url) + "contacts/"+email;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    handleGetContactsResult(response, jwt);
                },
                this::handleError){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    // Get info for all the other users in our contacts to fill out contact cards
    public void getContactCardInfo(final String jwt, final String memberId, boolean pending, boolean isMemberIdA){
        String url = getApplication().getResources().getString(R.string.base_url) + "contacts/info/"+memberId;

        JSONObject body = new JSONObject();
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                body,
                response -> {
                    handleResult(response, pending, isMemberIdA);
                },
                this::handleError){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    //add contacts - POST
    public void addContact(final String jwt, final String myEmail,final String userEmail) {
        String url = getApplication().getResources().getString(R.string.base_url) + "contacts/" + userEmail;
        JSONObject body = new JSONObject();
        try {
            body.put("myEmail", myEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse::setValue,
                error->{
                    handlePostError(error, mResponse);
                }) {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    // Update contacts - make the connection 'verified'
    public void updateContact(final String jwt, final String myEmail,final String userMemberId) {
        String url = getApplication().getResources().getString(R.string.base_url) + "contacts/"+myEmail+"/"+userMemberId;

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                mPutResponse::setValue,
                error->{
                    handlePostError(error, mPutResponse);
                }) {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }


    //delete contacts - remove connection
    public void deleteContact(final String jwt, final String myEmail,final String userMemberId) {
        String url = getApplication().getResources().getString(R.string.base_url) + "contacts/"+myEmail+"/"+userMemberId;

        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                mDeleteResponse::setValue,
                error->{
                    handlePostError(error, mDeleteResponse);
                }) {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    //handle delete contacts

    //search contacts

    //handle search contacts

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handlePostError(final VolleyError error, MutableLiveData<JSONObject> theResponse){
        if (Objects.isNull(error.networkResponse)) {
            try {
                theResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", error.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                theResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

}
