package brewin.mark.motionalarm;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

//Activity used to display that the alarm has been triggered.
public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";

    public double timeoutMins = 0.5;
    public double backupMins = 0.1;
    public double wakeDistance = 5;

    //Media player required to play the alarm sound.
    private MediaPlayer alarmMedPlyr;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location oldLocation;
    private Location newLocation;

    private Button alarmStop;
    private TextView alarmTime;
    private TextView countdownClock;
    private TextView triggerIteration;

    private WakeCheck wakeCheck;
    private CountDownTimer countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(TAG, "Alarm activity triggered.");

        //Creates the toolbar at the top of the application.
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        alarmStop = findViewById(R.id.btnStopAlarm);
        alarmTime = findViewById(R.id.txtAlarmTime);
        countdownClock = findViewById(R.id.txtCountdownClock);
        triggerIteration = findViewById(R.id.txtTriggerIteration);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {

                                newLocation = location;

                                Log.d(TAG, "Last known location retrieved.");
                                Log.d(TAG, "Last Location - Alt: " + location.getAltitude() + "\tLat: " +  location.getLatitude() + "\tLon: " + location.getLongitude());

                                getLocation();
                            }
                        }
                    });
        } else {
            Log.d(TAG, "Permissions not granted to access location!");
        }

        wakeCheck = new WakeCheck();

        alarmTime.setText(getString(R.string.alarm_time, wakeCheck.getTimeOriginal('h'), wakeCheck.getTimeOriginal('m')));
        triggerIteration.setText(getString(R.string.alarm_iteration, wakeCheck.getTriggerIteration()));

        alarmInitialiseSound();
        alarmStart();
    }

    private void alarmStart() {
        Log.d(TAG, "Alarm started.");

        triggerIteration.setText(getString(R.string.alarm_iteration, wakeCheck.getTriggerIteration()));

        if(countdown != null) {
            countdown.cancel();
        }

        try {
            alarmMedPlyr.prepare();
            alarmMedPlyr.start();
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "Error playing alarm ringtone.", Toast.LENGTH_LONG).show();
        }

        alarmStop.setEnabled(true);

        startTimeoutCountdown();
    }

    public void alarmBtnStop(View view){
        alarmStop();
    }

    //Stops the alarm activity and schedules the backup alarm.
    public void alarmStop() {
        Log.d(TAG, "Alarm turned off.");

        if(countdown != null) {
            countdown.cancel();
        }

        //Stop the alarm sound and disable the button.
        alarmMedPlyr.stop();
        alarmStop.setEnabled(false);

        startBackupCountdown();
    }

    private void alarmEnd() {
        Log.d(TAG, "Alarm destroyed.");

        alarmMedPlyr.stop();
        alarmMedPlyr.release();

        finish();
    }

    //Plays the alarm ringtone.
    private void alarmInitialiseSound() {
        //Fetches the default alarm ringtone and create the media player for it.
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        //In case no default alarm ringtone is found.
        if(alert == null) {
            //Fetch the notification ringtone.
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //In case no default notification is found.
            if(alert == null) {
                //Fetch the default ringtone.
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }


        try {
            //Creates the alarm media player with required properties.
            alarmMedPlyr = new MediaPlayer();
            alarmMedPlyr.setAudioStreamType(AudioManager.STREAM_ALARM);
            alarmMedPlyr.setLooping(true);
            alarmMedPlyr.setDataSource(getApplicationContext(), alert);
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "Error playing alarm ringtone.", Toast.LENGTH_LONG).show();
        }
    }

    private void startTimeoutCountdown() {
        Log.d(TAG, "Alarm timeout countdown started.");

        final double countdownTime = (timeoutMins * 60) * 1000;

        countdown = new CountDownTimer((long) countdownTime, 1000) {
            public void onTick(long millisUntilFinished) {
                long time = (long) countdownTime - millisUntilFinished;

                String mins = String.format("%02d", (time / 1000) / 60);
                String secs = String.format("%02d", (time / 1000) % 60);

                countdownClock.setText(getString(R.string.alarm_timeout, mins, secs));
            }

            public void onFinish() {
                Log.d(TAG, "Backup alarm triggered.");

                countdownClock.setText(getString(R.string.alarm_time, "00", "00"));

                getLocation();

                alarmStop();
            }
        }.start();
    }

    private void startBackupCountdown() {
        Log.d(TAG, "Backup alarm countdown started.");

        double countdownTime = (backupMins * 60) * 1000;

        countdown = new CountDownTimer((long) countdownTime, 1000) {
            public void onTick(long millisUntilFinished) {
                String mins = String.format("%02d", (millisUntilFinished / 1000) / 60);
                String secs = String.format("%02d", (millisUntilFinished / 1000) % 60);

                countdownClock.setText(getString(R.string.alarm_time, mins, secs));
            }

            public void onFinish() {
                Log.d(TAG, "Backup alarm triggered.");

                countdownClock.setText(getString(R.string.alarm_time, "00", "00"));

                getLocation();

                if(!wakeCheck.checkMovement(oldLocation, newLocation, wakeDistance)){
                    alarmStart();
                } else {
                    alarmEnd();
                }
            }
        }.start();
    }

    private void getLocation() {
        Log.d(TAG, "Fetching phone's location.");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    Log.d(TAG, "No location result retrieved.");
                } else {
                    Log.d(TAG, "Location retrieved.");

                    oldLocation = newLocation;
                    newLocation = locationResult.getLastLocation();

                    Log.d(TAG, "Old Location - Alt: " + oldLocation.getAltitude() + "\tLat: " +  oldLocation.getLatitude() + "\tLon: " + oldLocation.getLongitude());
                    Log.d(TAG, "New Location - Alt: " + newLocation.getAltitude() + "\tLat: " +  newLocation.getLatitude() + "\tLon: " + newLocation.getLongitude());
                }
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "Requesting location updates.");

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted. Getting location update.");

                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                } else {
                    Log.d(TAG, "Permissions not granted to access location!");
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to fetch location.");

                if(e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(AlarmActivity.this, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_end, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alarm_end:
                finish();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
