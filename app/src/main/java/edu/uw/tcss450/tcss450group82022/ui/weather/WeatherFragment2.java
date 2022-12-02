package edu.uw.tcss450.tcss450group82022.ui.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.tcss450group82022.R;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

@Metadata(
        mv = {1, 7, 1},
        k = 1,
        d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\rB\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0014R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u0004X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006¨\u0006\u000e"},
        d2 = {"Lcom/example/weatherapp/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "API", "", "getAPI", "()Ljava/lang/String;", "CITY", "getCITY", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "weatherTask", "UI_and_Navigation_Lab.app.main"}
)
public class WeatherFragment2 extends Fragment {
        @NotNull
        private final String CITY = "dhaka,bd";
        @NotNull
        private final String API = "06c921750b9a82d8f5d1294e1586276f";

        @NotNull
        public final String getCITY() {
                return this.CITY;
        }

        @NotNull
        public final String getAPI() {
                return this.API;
        }

        private View view;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                // Inflate the layout for this fragment

                this.view = inflater.inflate(R.layout.fragment_weather, container, false);
                (new WeatherFragment2.weatherTask()).execute(new String[0]);
                return view;
        }

        @Metadata(
                mv = {1, 7, 1},
                k = 1,
                d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0004J'\u0010\u0005\u001a\u0004\u0018\u00010\u00022\u0016\u0010\u0006\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00020\u0007\"\u0004\u0018\u00010\u0002H\u0014¢\u0006\u0002\u0010\bJ\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0002H\u0014J\b\u0010\f\u001a\u00020\nH\u0014¨\u0006\r"},
                d2 = {"Lcom/example/weatherapp/MainActivity$weatherTask;", "Landroid/os/AsyncTask;", "", "Ljava/lang/Void;", "(Lcom/example/weatherapp/MainActivity;)V", "doInBackground", "params", "", "([Ljava/lang/String;)Ljava/lang/String;", "onPostExecute", "", "result", "onPreExecute", "UI_and_Navigation_Lab.app.main"}
        )
        public final class weatherTask extends AsyncTask {
                protected void onPreExecute() {
                        super.onPreExecute();
                        View var10000 = view.findViewById(R.id.loader);
                        Intrinsics.checkNotNullExpressionValue(var10000, "view.findViewById(R.id.loader)");
                        ((ProgressBar)var10000).setVisibility(View.VISIBLE);
                        var10000 = view.findViewById(R.id.mainContainer);
                        Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<RelativeLayout>(R.id.mainContainer)");
                        ((RelativeLayout)var10000).setVisibility(View.GONE);
                        var10000 = view.findViewById(R.id.errorText);
                        Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.errorText)");
                        ((TextView)var10000).setVisibility(View.GONE);
                }

                @Nullable
                protected String doInBackground(@NotNull String... params) {
                        Intrinsics.checkNotNullParameter(params, "params");
                        String response = null;

                        try {
                                URL var3 = new URL("https://api.openweathermap.org/data/2.5/weather?q=seattle&forecast?id=524901&appid=fe5e828e566e38d0c546d2d533dcc41b");
                                Charset var4 = Charsets.UTF_8;
                                byte[] var5 = TextStreamsKt.readBytes(var3);
                                response = new String(var5, var4);
                        } catch (Exception var6) {
                                response = (String)null;
                        }

                        return response;
                }

                // $FF: synthetic method
                // $FF: bridge method
                public Object doInBackground(Object[] var1) {
                        return this.doInBackground((String[])var1);
                }
                private String convertKelvinToFarenheit(String celc) {

                        return String.format("%.1f", (1.8*(Double.valueOf(celc) - 273) + 32));

                }

                protected void onPostExecute(@Nullable String result) {
                        super.onPostExecute(result);

                        View var10000;
                        try {
                                JSONObject jsonObj = new JSONObject(result);
                                JSONObject main = jsonObj.getJSONObject("main");
                                JSONObject sys = jsonObj.getJSONObject("sys");
                                JSONObject wind = jsonObj.getJSONObject("wind");
                                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);
                                long updatedAt = jsonObj.getLong("dt");
                                String updatedAtText = "Updated at: " + (new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)).format(new Date(updatedAt * (long)1000));
                                String temp = convertKelvinToFarenheit(main.getString("temp")) + "°F";
                                String tempMin = "Min Temp: " + convertKelvinToFarenheit(main.getString("temp_min")) + "°F";
                                String tempMax = "Max Temp: " + convertKelvinToFarenheit(main.getString("temp_max")) + "°F";
                                String pressure = main.getString("pressure");
                                String humidity = main.getString("humidity");
                                long sunrise = sys.getLong("sunrise");
                                long sunset = sys.getLong("sunset");
                                String windSpeed = wind.getString("speed");
                                String weatherDescription = weather.getString("description");
                                String address = jsonObj.getString("name") + ", " + sys.getString("country");
                                var10000 = getView().findViewById(R.id.address);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.address)");
                                ((TextView)var10000).setText((CharSequence)address);
                                var10000 = getView().findViewById(R.id.updated_at);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.updated_at)");
                                ((TextView)var10000).setText((CharSequence)updatedAtText);
                                var10000 = getView().findViewById(R.id.status);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.status)");
                                TextView var23 = (TextView)var10000;
                                Intrinsics.checkNotNullExpressionValue(weatherDescription, "weatherDescription");
                                var23.setText((CharSequence)StringsKt.capitalize(weatherDescription));
                                var10000 = getView().findViewById(R.id.temp);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.temp)");
                                ((TextView)var10000).setText((CharSequence)temp);
                                var10000 = getView().findViewById(R.id.temp_min);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.temp_min)");
                                ((TextView)var10000).setText((CharSequence)tempMin);
                                var10000 = getView().findViewById(R.id.temp_max);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.temp_max)");
                                ((TextView)var10000).setText((CharSequence)tempMax);
                                var10000 = getView().findViewById(R.id.sunrise);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.sunrise)");
                                ((TextView)var10000).setText((CharSequence)(new SimpleDateFormat("hh:mm a", Locale.ENGLISH)).format(new Date(sunrise * (long)1000)));
                                var10000 = getView().findViewById(R.id.sunset);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.sunset)");
                                ((TextView)var10000).setText((CharSequence)(new SimpleDateFormat("hh:mm a", Locale.ENGLISH)).format(new Date(sunset * (long)1000)));
                                var10000 = getView().findViewById(R.id.humidity);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.humidity)");
                                ((TextView)var10000).setText((CharSequence)humidity);
                                var10000 = getView().findViewById(R.id.loader);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<ProgressBar>(R.id.loader)");
                                ((ProgressBar)var10000).setVisibility(View.GONE);
                                var10000 = getView().findViewById(R.id.mainContainer);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<RelativeLayout>(R.id.mainContainer)");
                                ((RelativeLayout)var10000).setVisibility(View.VISIBLE);
                        } catch (Exception var22) {
                                var10000 = getView().findViewById(R.id.loader);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<ProgressBar>(R.id.loader)");
                                ((ProgressBar)var10000).setVisibility(View.GONE);
                                var10000 = getView().findViewById(R.id.errorText);
                                Intrinsics.checkNotNullExpressionValue(var10000, "findViewById<TextView>(R.id.errorText)");
                                ((TextView)var10000).setVisibility(View.VISIBLE);
                        }

                }

                // $FF: synthetic method
                // $FF: bridge method
                public void onPostExecute(Object var1) {
                        this.onPostExecute((String)var1);
                }
        }
}
