package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> alarms = new ArrayList<>();

    private AlarmAdapter alarmAdapter;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarms.add("Hello");
        alarms.add("Test");
        alarms.add("Frank.");

        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        FloatingActionButton fab = findViewById(R.id.fabAddAlarm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAlarm();
            }
        });

        if(!alarms.isEmpty()) {
            findViewById(R.id.txtNoAlarms).setVisibility(View.GONE);

            ListView alarmList = findViewById(R.id.listAlarms);
            alarmAdapter = new AlarmAdapter(this, R.layout.list_alarm, alarms);
            alarmList.setAdapter(alarmAdapter);
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

    private void addAlarm() {
        Toast.makeText(getApplicationContext(), "Create Alarm", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), CreateAlarmActivity.class);
        startActivity(intent);
    }
}