package edu.uw.tcss450.tcss450group82022.ui.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.tcss450group82022.R;

public class ChatListViewModel extends AndroidViewModel {
    private MutableLiveData<List<ChatCard>> mChatList;
    private MutableLiveData<String> mChatId;
    private MutableLiveData<JSONObject> mPutResponse;
    private MutableLiveData<JSONObject> mDeleteResponse;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatList = new MutableLiveData<>();
        mChatList.setValue(new ArrayList<>());
        mChatId = new MutableLiveData<>();
        mChatId.setValue("");
        mPutResponse = new MutableLiveData<>();
        mPutResponse.setValue(new JSONObject());
        mDeleteResponse = new MutableLiveData<>();
        mDeleteResponse.setValue(new JSONObject());
    }
    // OBSERVERS FOR REQUESTS - CALLED SOMEWHERE ELSE ----------------------------------------------

    public void addChatListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<ChatCard>> observer) {
        mChatList.observe(owner, observer);
    }

    public void addChatIdObserver(@NonNull LifecycleOwner owner,
                                  @NonNull Observer<? super String> observer){
        mChatId.observe(owner, observer);
    }

    public void addPutResponseObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super JSONObject> observer){
        mPutResponse.observe(owner, observer);
    }

    public void addDeleteResponseObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super JSONObject> observer){
        mDeleteResponse.observe(owner, observer);
    }
    // OBSERVER END --------------------------------------------------------------------------------

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    /**
     * Handles the result from the GET HTTP Request
     * Formats the request and turns it into cards for RecyclerView
     * @param result
     */
    private void handleResult(final JSONObject result) {
        mChatList.setValue(new ArrayList<>());
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            Log.e("CHATID", "Root: " + root);
            // Collect Chat Ids
            if (root.has(getString.apply(R.string.keys_json_chats_chatIdList)) &&
                    root.has(getString.apply(R.string.keys_json_chats_chatNameList))) {
                Log.e("CHATID", "Passed");
                JSONArray chatIdData = root.getJSONArray(
                        getString.apply(R.string.keys_json_chats_chatIdList));
                JSONArray chatNameData = root.getJSONArray(
                        getString.apply(R.string.keys_json_chats_chatNameList));
                for(int i = 0; i < chatIdData.length(); i++) {
                    JSONObject jsonChatId = chatIdData.getJSONObject(i);
                    JSONObject jsonChatName = chatNameData.getJSONObject(i);
                    ChatCard chatCard = new ChatCard.Builder(
                            jsonChatId.getString(
                                    getString.apply(
                                            R.string.keys_json_chats_chatId)),
                            jsonChatName.getString(
                                    getString.apply(
                                            R.string.keys_json_chats_name)))
                            .build();
                    if (!mChatList.getValue().contains(chatCard)) {
                        mChatList.getValue().add(chatCard);
                    }
                    Log.i("CHAT", "ChatList: " + mChatList);
                }
            } else {
                Log.e("ERROR!", "No data array");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
        mChatList.setValue(mChatList.getValue());
    }

    /**
     * Handles the results from the POST HTTP call
     * Assigns the new chatId to the MutableLiveData to be accessed elsewhere
     * @param result
     */
    private void handlePostResult(final JSONObject result) {
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            Log.i("CHATID", "Root of post result: " + root);
            if (root.has("chatID")){
                Log.i("CHATID", "Updating chatId");
                mChatId.setValue(root.getString("chatID"));
            }

        } catch (JSONException e){
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }
    }

    /**
     * GET HTTP Request
     * Used to get a list of chatId's and Names of chat rooms
     * Stored in the SQL database
     * @param jwt
     * @param email
     */
    public void connectGet(final String jwt, final String email) {
        String url =
                getApplication().getResources().getString(R.string.base_url)+"chats/"+email;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
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

    /**
     * POST HTTP Request, uses a name and creates a new ChatId in
     * the SQL database.
     * Returns the ChatID
     * @param jwt
     * @param name
     */
    public void connectPost(final String jwt, final String name){
        String url =
                getApplication().getResources().getString(R.string.base_url)+"chats";
        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handlePostResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
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

    /**
     * PUT HTTP Request, adds a user to the chat room associated with provided chatId
     * Sets a success response if it works, which is a LiveMutableData
     * @param jwt
     * @param chatId
     */
    public void connectPut(final String jwt, final String chatId){
        String url =
                getApplication().getResources().getString(R.string.base_url)+"chats/"+chatId;
        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null, //no body for this get request
                mPutResponse::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
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

    /**
     * DELETE HTTP Request, uses a chatId to delete the associated chat room
     * Gets rid of everyone inside the chat room, and all messages associated with it
     * @param jwt
     * @param chatId
     */
    public void connectDelete(final String jwt, final String chatId){
        String url =
                getApplication().getResources().getString(R.string.base_url)+"chats/"+chatId;
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
                mDeleteResponse::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
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
}
