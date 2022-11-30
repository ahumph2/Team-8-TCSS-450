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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwelveHourHomeViewModel extends AndroidViewModel {

    private MutableLiveData<JSONArray> mDetails;

    public TwelveHourHomeViewModel(@NonNull Application application) {
        super(application);
        mDetails = new MutableLiveData<>();
        mDetails.setValue(new JSONArray());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONArray> observer) {
        mDetails.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PTOJECT
        Log.e("twlevehourhomeviewmodel", error.getLocalizedMessage());

        //throw new IllegalStateException(error.getMessage());
    }







    public void connect(final String locationKey){
        String url = "https://mobileapp-group-backend.herokuapp.com/twelvehour";

        JSONObject body = new JSONObject();
        try{
            body.put("locationkey", locationKey);
        } catch (JSONException e){
            e.printStackTrace();
        }

        CustomJsonArrayRequest request = new CustomJsonArrayRequest(
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

        Log.d("twelvehourhomeviewmodel mDetails", mDetails.toString());
    }

    private void handleResult(JSONObject result) {


        Log.d("twelvehourhomeviewmodel handleResult", result.toString());
    }
}
