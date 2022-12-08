package edu.uw.tcss450.tcss450group82022;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import edu.uw.tcss450.tcss450group82022.utils.Utils;
import edu.uw.tcss450.tcss450group82022.model.LocationViewModel;
import edu.uw.tcss450.tcss450group82022.model.WeatherProfileViewModel;
import edu.uw.tcss450.tcss450group82022.databinding.ActivityMainBinding;
import edu.uw.tcss450.tcss450group82022.model.NewMessageCountViewModel;
import edu.uw.tcss450.tcss450group82022.model.PushyTokenViewModel;
import edu.uw.tcss450.tcss450group82022.model.UserInfoViewModel;
import edu.uw.tcss450.tcss450group82022.R;
import edu.uw.tcss450.tcss450group82022.services.PushReceiver;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatMessage;
import edu.uw.tcss450.tcss450group82022.ui.chat.ChatViewModel;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private MainPushMessageReceiver mPushMessageReceiver;
    private NewMessageCountViewModel mNewMessageModel;

    private ActivityMainBinding binding;

    private static final int MY_PERMISSIONS_LOCATIONS = 8414;

    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        // Import com.auth0.android.jwt.JWT
        JWT jwt = new JWT(args.getJwt());

        // Check to see if the web token is still valid or not. To make a JWT expire after
        // a longer or shorter time period; change the expiration time when the JWT is
        // created on the web service.
        if(Objects.requireNonNull(jwt.getExpiresAt()).after(jwt.getIssuedAt())){
            new ViewModelProvider(this,
                    new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt())
            ).get(UserInfoViewModel.class);
        } else {
            // In production code, add in your own error handling/flow for when the JWT is expired
            throw new IllegalStateException("JWT is expired!");
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_incrementor, R.id.navigation_chats)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        Log.w("", ""); //Empty code for GitHub practice

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_chats) {
                //When the user navigates to the chats page, reset the new message count.
                //This will need some extra logic for your project as it should have
                //multiple chat rooms.
                mNewMessageModel.reset();
            }
        });

        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = binding.navView.getOrCreateBadge(R.id.navigation_chats);
            badge.setMaxCharacterCount(2);
            if (count > 0) {
                //new messages! update and show the notification badge.
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                //user did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_LOCATIONS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the locations-related task you need to do.
                requestLocation();
            } else {
                // permission denied, boo! Disable the functionality that depends on this permission.
                Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                // TODO Inform user that app needs location permissions
                finishAndRemoveTask();
            }
        } // Add other request permissions to listen to here as else (or convert to switch statement)
    }
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            WeatherProfileViewModel weatherModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new WeatherProfileViewModel.WeatherFactory(getApplication()))
                    .get(WeatherProfileViewModel.class);

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d("REQUESTED_LOCATION", location.toString());

                                LocationViewModel LocModel = LocationViewModel.getFactory().create(LocationViewModel.class);
                                LocModel.changeLocation(location);

                                // Get saved weather info view model from SharedPreferences and check for update:

                                Utils.updateWeatherIfNecessary(weatherModel);
                            } else if (location == null) {
                                Log.d("LOCATION IS NULL:",  "ohnono");
                            }
                        }
                    });
                   /*  location -> {
                Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Log.d("REQUESTED_LOCATION", location.toString());

                    LocationViewModel LocModel = LocationViewModel.getFactory().create(LocationViewModel.class);
                    LocModel.changeLocation(location);

                    // Get saved weather info view model from SharedPreferences and check for update:
                    WeatherProfileViewModel weatherModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new WeatherProfileViewModel.WeatherFactory(getApplication()))
                            .get(WeatherProfileViewModel.class);
                    Utils.updateWeatherIfNecessary(weatherModel);
                } else if (location == null) {
                    Log.d("LOCATION IS NULL:",  "ohnono");
                }
});
                 */

        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
            || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //TODO open a settings fragment
            Log.d("SETTINGS", "Clicked");
            return true;
        }
        else if(id == R.id.action_sign_out) {
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();
        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);
        //when we hear back from the web service quit
        model.addResponseObserver(this, result -> finishAndRemoveTask());
        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class)
                        .getmJwt()
        );
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {
        private ChatViewModel mModel =
                new ViewModelProvider(MainActivity.this)
                        .get(ChatViewModel.class);
        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination nd = nc.getCurrentDestination();
            if (intent.hasExtra("chatMessage")) {
                ChatMessage cm = (ChatMessage) intent.getSerializableExtra("chatMessage");
                //If the user is not on the chat screen, update the
                // NewMessageCountView Model
                if (nd.getId() != R.id.navigation_chats) {
                    mNewMessageModel.increment();
                }
                //Inform the view model holding chatroom messages of the new
                //message.
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
    }
}
