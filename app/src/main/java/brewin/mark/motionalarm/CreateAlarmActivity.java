package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.app.AlarmManager.*;

//Activity used to edit and create new alarms.
public class CreateAlarmActivity extends AppCompatActivity {
    private static final String TAG = "CreateAlarmActivity";

    AlarmManager alarmManager;
    private TimePicker timePicker;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        //Fetches the alarm service so the new alarm can be created.
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Gets the time picker and name for later use. Also sets the time picker in 24hour format.
        timePicker = findViewById(R.id.timeAlarm);
        editText = findViewById(R.id.editName);
        //Also checks 24 hours is a valid format for the user.
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
    }

    public void alarmCreate(View view) {
        Log.d(TAG, "New alarm being created.");
        //Gets the time for the alarm being created.
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        time.set(Calendar.MINUTE, timePicker.getMinute());
        String name;

        if(TextUtils.isEmpty(editText.getText())) {
            name = "New Alarm";
        } else {
            name = editText.getText().toString();
        }

        Intent replyIntent = new Intent();
        replyIntent.putExtra("name", name);
        replyIntent.putExtra("time", time.getTimeInMillis());
        setResult(RESULT_OK, replyIntent);

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
                Intent replyIntent = new Intent();
                setResult(RESULT_CANCELED, replyIntent);

                finish();

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
