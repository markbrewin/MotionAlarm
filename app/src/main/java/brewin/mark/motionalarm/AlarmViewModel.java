package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class AlarmViewModel extends AndroidViewModel {
    private static final String TAG = "AlarmViewModel";

    private AlarmRepository mRepository;
    private LiveData<List<Alarm>> mAllAlarms;

    public AlarmViewModel(Application application) {
        super(application);
        mRepository = new AlarmRepository(application);
        mAllAlarms = mRepository.getAllAlarms();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    public void insert(Alarm alarm) {
        Log.d(TAG, "Creating alarm intents.");

        final Context context = getApplication().getApplicationContext();

        Intent alarmActivity = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, alarmActivity, 0);

        Intent editAlarmActivity = new Intent(context, CreateAlarmActivity.class);
        PendingIntent editAlarmIntent = PendingIntent.getActivity(context, 0, editAlarmActivity, 0);

        //Assigns the time and intents to the alarm.
        Log.d(TAG, "Setting alarm properties.");
        AlarmManager.AlarmClockInfo alarmInfo = new AlarmManager.AlarmClockInfo(alarm.getTime().getTimeInMillis(), editAlarmIntent);

        //Creates and sets the alarm clock in the alarm manager service.
        Log.d(TAG, "Finalising creation.");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setAlarmClock(alarmInfo, alarmIntent);

        mRepository.insert(alarm);
    }
}
