package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayList<String> alarms = new ArrayList<>();

    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates the toolbar at the top of the application.
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        //Fetches the alarm manager service.
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarms.add("Hello");
        alarms.add("Test");
        alarms.add("Frank.");

        //Checks if any alarms exist and whether to display this.
        if(!alarms.isEmpty()) {
            Log.d(TAG, "Alarms exist and will be displayed.");
            findViewById(R.id.txtNoAlarms).setVisibility(View.GONE);

            //Lists each alarm using the custom layout.
            ListView alarmList = findViewById(R.id.listAlarms);
            AlarmAdapter alarmAdapter = new AlarmAdapter(this, R.layout.list_alarm, alarms);
            alarmList.setAdapter(alarmAdapter);
        } else {
            Log.d(TAG, "No alarms exist to be displayed.");
        }
    }

   /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_add, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alarm_add:
                Toast.makeText(getApplicationContext(), "Create Alarm", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void addAlarm(View view) {
        Log.d(TAG, "CreateAlarm activity started.");

        Intent intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
        startActivity(intent);
    }
}