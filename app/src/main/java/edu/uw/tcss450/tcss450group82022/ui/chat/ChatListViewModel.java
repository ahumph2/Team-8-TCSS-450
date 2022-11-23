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

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatList = new MutableLiveData<>();
        mChatList.setValue(new ArrayList<>());
        mChatId = new MutableLiveData<>();
        mChatId.setValue("");
        mPutResponse = new MutableLiveData<>();
        mPutResponse.setValue(new JSONObject());
    }
    public void addBlogListObserver(@NonNull LifecycleOwner owner,
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
    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handleResult(final JSONObject result) {
        mChatList.setValue(new ArrayList<>());
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            if (root.has(getString.apply(R.string.keys_json_chats_rows))) {
                JSONArray data = root.getJSONArray(
                        getString.apply(R.string.keys_json_chats_rows));
                for(int i = 0; i < data.length(); i++) {
                    JSONObject jsonChat = data.getJSONObject(i);
                    Log.i("DATA", "jsonChat: " + jsonChat );
                    ChatCard chatCard = new ChatCard.Builder(
                            jsonChat.getString(
                                    getString.apply(
                                            R.string.keys_json_chats_chatId)))
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
}
