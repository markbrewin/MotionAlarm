package brewin.mark.motionalarm;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int NEW_ALARM = 1;

    private TextView noAlarms;
    private RecyclerView alarmList;
    private AlarmViewModel mAlarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates the toolbar at the top of the application.
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        checkPermissions();

        noAlarms = findViewById(R.id.txtNoAlarms);
        alarmList = findViewById(R.id.listAlarms);

        final AlarmAdapter adapter = new AlarmAdapter(this);
        alarmList.setAdapter(adapter);
        alarmList.setLayoutManager(new LinearLayoutManager(this));

        mAlarmViewModel = ViewModelProviders.of(this).get(AlarmViewModel.class);

        mAlarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@Nullable List<Alarm> alarms) {
                Log.d(TAG, "Updated alarms list.");

                adapter.setAlarms(alarms);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReciever,
                new IntentFilter("DeleteAlarm"));
    }

    public void addAlarm(View view) {
        Log.d(TAG, "CreateAlarm activity started.");

        Intent intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
        startActivityForResult(intent, NEW_ALARM);
    }

    public void deleteAlarm(int id) {
        mAlarmViewModel.delete(id);
        mAlarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(@Nullable List<Alarm> alarms) {
                if(alarms != null) {
                    checkNextAlarm();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Activity result created, alarm creation finished.");

        if(requestCode == NEW_ALARM && resultCode == RESULT_OK) {
            Log.d(TAG, "Adding new alarm to database.");

            Bundle extras = data.getExtras();
            String name = extras.getString("name");
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(extras.getLong("time"));

            Alarm alarm = new Alarm(name, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
            mAlarmViewModel.insert(alarm);
            mAlarmViewModel.getAllAlarms().observe(this, new Observer<List<Alarm>>() {
                @Override
                public void onChanged(@Nullable List<Alarm> alarms) {
                    checkNextAlarm();
                }
            });
        } else {
            Log.d(TAG, "Alarm creation cancelled.");
        }
    }

    private void checkNextAlarm() {
        mAlarmViewModel.getNextAlarm().observe(this, new Observer<Alarm>() {
            @Override
            public void onChanged(@Nullable Alarm alarm) {
                if(alarm != null) {
                    mAlarmViewModel.setAlarmInManager(alarm);
                }
            }
        });
    }

    public BroadcastReceiver mMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int id = extras.getInt("id");

            deleteAlarm(id);
        }
    };

    private void checkPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                },1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted.");
                } else {
                    Log.d(TAG, "Permissions denied.");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.alarm_add:
                intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
                startActivity(intent);

                return true;
            case R.id.add_google_cal:
                intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);

                return true;
            case R.id.settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}