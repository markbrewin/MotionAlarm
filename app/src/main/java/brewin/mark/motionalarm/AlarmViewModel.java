package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class AlarmViewModel extends AndroidViewModel {
    private static final String TAG = "AlarmViewModel";

    private AlarmRepository mRepository;
    private LiveData<List<Alarm>> mAllAlarms;
    private LiveData<Alarm> curAlarm;
    private LiveData<Alarm> nextAlarm;

    public AlarmViewModel(Application application) {
        super(application);
        mRepository = new AlarmRepository(application);
        mAllAlarms = mRepository.getAllAlarms();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    public LiveData<Alarm> getCurAlarm(Calendar time) {
        curAlarm = mRepository.getCurAlarm(time);

        return curAlarm;
    }

    public LiveData<Alarm> getNextAlarm() {
        nextAlarm = mRepository.getNextAlarm();

        return nextAlarm;
    }

    public void insert(Alarm alarm) {
        Log.d(TAG, "Creating new alarm.");

        mRepository.insert(alarm);
    }

    public void delete(Alarm alarm) {
        Log.d(TAG, "Deleting and setting next alarm.");

        mRepository.delete(alarm);
    }

    public void setAlarmInManager(Alarm alarm) {
        Log.d(TAG, "Creating next alarm in AlarmManager.");
        final Context context = getApplication().getApplicationContext();

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        time.set(Calendar.MINUTE, alarm.getMin());

        Log.d(TAG, "Creating alarm intents.");
        Intent alarmActivity = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, alarmActivity, 0);

        Intent editAlarmActivity = new Intent(context, CreateAlarmActivity.class);
        PendingIntent editAlarmIntent = PendingIntent.getActivity(context, 0, editAlarmActivity, 0);

        //Assigns the time and intents to the alarm.
        Log.d(TAG, "Setting alarm properties.");
        AlarmManager.AlarmClockInfo alarmInfo = new AlarmManager.AlarmClockInfo(time.getTimeInMillis(), editAlarmIntent);

        //Creates and sets the alarm clock in the alarm manager service.
        Log.d(TAG, "Finalising creation.");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setAlarmClock(alarmInfo, alarmIntent);
    }
}
