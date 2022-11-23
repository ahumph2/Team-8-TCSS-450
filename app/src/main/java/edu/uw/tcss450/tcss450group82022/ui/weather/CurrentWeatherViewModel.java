package edu.uw.tcss450.tcss450group82022.ui.weather;

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
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import kotlin.text.Charsets;


public class CurrentWeatherViewModel extends AndroidViewModel {
    private String KEY = "fe5e828e566e38d0c546d2d533dcc41b";
    private String CITY = "seattle, wa";

    private MutableLiveData<JSONObject> mDetails;

    public CurrentWeatherViewModel(@NonNull Application application) {
        super(application);

        mDetails = new MutableLiveData<JSONObject>();
        mDetails.setValue(new JSONObject());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {

        mDetails.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PTOJECT
        Log.e("currentweatherviewmodel", error.getLocalizedMessage());

        //throw new IllegalStateException(error.getMessage());
    }




    public void connect(final String locationKey){
        String url = String.format("https://api.openweathermap.org/data/2.5/forecast?id=524901&appid=%s", KEY);
        String response;

        Log.e("Api", url);

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
        Log.i("response", request.getBodyContentType());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
}


