package brewin.mark.motionalarm;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

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
                adapter.setWords(alarms);
            }
        });
    }

    public void addAlarm(View view) {
        Log.d(TAG, "CreateAlarm activity started.");

        Intent intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
        startActivity(intent);
    }

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
        switch (item.getItemId()) {
            case R.id.alarm_add:
                Intent intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
                startActivity(intent);

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}