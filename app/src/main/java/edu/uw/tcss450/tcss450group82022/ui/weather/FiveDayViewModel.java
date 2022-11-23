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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import edu.uw.tcss450.tcss450group82022.R;


public class FiveDayViewModel extends AndroidViewModel {

    private MutableLiveData<List<DayPost>> mDayList;
    public FiveDayViewModel(@NonNull Application application) {
        super(application);
        mDayList = new MutableLiveData<>();
        mDayList.setValue(new ArrayList<>());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<DayPost>> observer) {
        mDayList.observe(owner, observer);
    }



    private void handleError(final VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PTOJECT
        Log.e("fivedayviewmodel", error.getLocalizedMessage());

        //throw new IllegalStateException(error.getMessage());
    }

    private void handleResult(final JSONObject result){
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        Log.d("fivedayviewmodel handleResult result ", result.toString());
         try{
            JSONArray data = result.getJSONArray("DailyForecasts");
             Log.d("fivedayviewmodel handleResult dailyforecast ", data.toString());

            for(int i=0; i <data.length();i++){
                JSONObject jsonDay = data.getJSONObject(i);
                JSONObject temp = jsonDay.getJSONObject("Temperature");
                JSONObject max = temp.getJSONObject("Maximum");
                JSONObject min = temp.getJSONObject("Minimum");
                JSONObject day = jsonDay.getJSONObject("Day");
                JSONObject night = jsonDay.getJSONObject("Night");
                Log.d("fivedayviewmodel handleResult data ", jsonDay.toString());

                DayPost post = new DayPost.Builder(
                        //TODO:
                        jsonDay.getString(
                                getString.apply(
                                        R.string.keys_json_weather_date)),
                        max.getString(
                                getString.apply(
                                        R.string.keys_json_weather_value)),
                        min.getString(
                                getString.apply(
                                        R.string.keys_json_weather_value)),
                        day.getString(
                                getString.apply(
                                        R.string.keys_json_weather_condition)),
                        night.getString(
                                getString.apply(
                                        R.string.keys_json_weather_condition)))
                                .build();
                if(!mDayList.getValue().contains(post)){
                    mDayList.getValue().add(post);
                }

            }
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }


        mDayList.setValue(mDayList.getValue());
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
