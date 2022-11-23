package edu.uw.tcss450.uiandnavigationlab.ui.weather;

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

import org.json.JSONException;
import org.json.JSONObject;

public class FiveDayHomeViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mDetails;

    public FiveDayHomeViewModel(@NonNull Application application) {
        super(application);
        mDetails = new MutableLiveData<>();
        mDetails.setValue(new JSONObject());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mDetails.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PTOJECT
        Log.e("fivedayhomeviewmodel", error.getLocalizedMessage());

       // throw new IllegalStateException(error.getMessage());
    }

    public void connect(final String locationKey){
        String url = "https://api.openweathermap.org/data/2.5/weather?q=seattle&forecast?id=524901&appid=fe5e828e566e38d0c546d2d533dcc41b";

        JSONObject body = new JSONObject();
        try{
            body.put("locationkey", locationKey);
        } catch (JSONException e){
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mDetails::setValue,
                this::handleError
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);

        Log.d("JSON RESPONSE", mDetails.toString());
    }


}

