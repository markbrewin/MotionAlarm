package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.app.AlarmManager.*;

//Activity used to edit and create new alarms.
public class CreateAlarmActivity extends AppCompatActivity {

    private static final String TAG = "CreateAlarmActivity";

    AlarmManager alarmManager;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        //Fetches the alarm service so the new alarm can be created.
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Gets the time picker so the user's selected time can be used.
        timePicker = findViewById(R.id.timeAlarm);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
    }

    public void alarmCreate(View view) {
        Log.d(TAG, "New alarm being created.");
        //Gets the time for the alarm being created.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        //Sets the associated intents and receivers for the alarm.
        Log.d(TAG, "Creating alarm intents.");
        Intent alarmActivity = new Intent(CreateAlarmActivity.this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(CreateAlarmActivity.this, 0, alarmActivity, 0);

        Intent editAlarmActivity = new Intent(CreateAlarmActivity.this, CreateAlarmActivity.class);
        PendingIntent editAlarmIntent = PendingIntent.getActivity(CreateAlarmActivity.this, 0, editAlarmActivity, 0);

        //Assigns the time and intents to the alarm.
        Log.d(TAG, "Setting alarm properties.");
        AlarmClockInfo alarmInfo = new AlarmClockInfo(calendar.getTimeInMillis(), editAlarmIntent);

        //Creates and sets the alarm clock in the alarm manager service.
        Log.d(TAG, "Finalising creation.");
        alarmManager.setAlarmClock(alarmInfo, alarmIntent);

        //Returns the user back to the main activity.
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                finish();

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
