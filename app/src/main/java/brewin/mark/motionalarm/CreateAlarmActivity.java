package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.app.AlarmManager.*;

public class CreateAlarmActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private TimePicker timePicker;
    private PendingIntent alarmIntent;
    private PendingIntent editAlarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        timePicker = findViewById(R.id.timeAlarm);

        Button createAlarm = findViewById(R.id.btnSetAlarm);
        createAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });
    }

    public void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        Intent alarmActivity = new Intent(CreateAlarmActivity.this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(CreateAlarmActivity.this, 0, alarmActivity, 0);

        Intent editAlarmActivity = new Intent(CreateAlarmActivity.this, CreateAlarmActivity.class);
        editAlarmIntent = PendingIntent.getActivity(CreateAlarmActivity.this, 0, editAlarmActivity, 0);

        AlarmClockInfo alarmInfo = new AlarmClockInfo(calendar.getTimeInMillis(), editAlarmIntent);

        alarmManager.setAlarmClock(alarmInfo, alarmIntent);
    }
}
