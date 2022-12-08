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

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>(new JSONObject());
    }
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<ContactCard>> observer) {
        mContactList.observe(owner, observer);
    }

    public void addPostResponseObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super JSONObject> observer){
        mResponse.observe(owner, observer);
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
     * Add the contacts from JSONArray array to the mContactList
     * @param
     */
    private void handleResult(final JSONObject response){
        JSONObject root = response;
        try{
            if(root.has("contacts")){
                JSONArray contactsArray = root.getJSONArray("contacts");

                for(int i = 0; i < contactsArray.length(); i++){
                    JSONObject contact = contactsArray.getJSONObject(i);
                    ContactCard contactCard = new ContactCard(
                            contact.getString("firstname"),
                            contact.getString("lastname"),
                            contact.getString("email"),
                            contact.getInt("memberid")
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

    public void getContacts(final String jwt, final String email) {
        String url = getApplication().getResources().getString(R.string.base_url) + "contacts/"+email;

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleResult,
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
                this::handlePostError) {
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


    //delete contacts

    //handle delete contacts

    //search contacts

    //handle search contacts

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handlePostError(final VolleyError error){
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
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
                mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

}
