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

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import edu.uw.tcss450.tcss450group82022.R;

public class TwelveHourViewModel extends AndroidViewModel {

    private MutableLiveData<List<HourPost>> mHourList;

    public TwelveHourViewModel(@NonNull Application application) {
        super(application);
        mHourList = new MutableLiveData<>();
        mHourList.setValue(new ArrayList<>());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<HourPost>> observer) {
        mHourList.observe(owner, observer);
    }

    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PTOJECT
        Log.e("twelevehourviewmodel", error.getLocalizedMessage());

        //throw new IllegalStateException(error.getMessage());
    }

    private void handleResult(final JSONArray result){
        IntFunction<String> getString =
                getApplication().getResources()::getString;
            Log.d("TwelveHourViewModel handleResult result", result.toString());
        try{
             for(int i= 0; i < result.length(); i++) {
                JSONObject jsonHour = result.getJSONObject(i);
                JSONObject temp = jsonHour.getJSONObject("Temperature");
                HourPost post = new HourPost.Builder(
                        jsonHour.getString(
                                getString.apply(
                                        R.string.keys_json_weather_dateTime)),
                        temp.getString(
                                getString.apply(
                                        R.string.keys_json_weather_value)),
                        jsonHour.getString(
                                getString.apply(R.string.keys_json_weather_condition)))
                        .build();
                if(!mHourList.getValue().contains(post)){
                    mHourList.getValue().add(post);
                }

            }
        } catch (JSONException e){
            Log.e("ERROR!", e.getMessage());
        }

        mHourList.setValue(mHourList.getValue());

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
                this::handleResult,
                this::handleError
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
}
