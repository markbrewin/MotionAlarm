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
    private LiveData<List<Trivia>> mAllTrivia;
    private LiveData<Alarm> curAlarm;
    private LiveData<Alarm> nextAlarm;

    public AlarmViewModel(Application application) {
        super(application);
        mRepository = new AlarmRepository(application);
        mAllAlarms = mRepository.getAllAlarms();
        mAllTrivia = mRepository.getAllTrivia();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    LiveData<List<Trivia>> getAllTrivia() {
        return mAllTrivia;
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

    public void insertTrivia(String question, String answer) {
        Log.d(TAG, "Creating new trivia.");

        Trivia trivia = new Trivia(question, answer);

        mRepository.insertTrivia(trivia);
    }

    public void delete(Alarm alarm) {
        Log.d(TAG, "Deleting and setting next alarm.");

        mRepository.delete(alarm);

        final Context context = getApplication().getApplicationContext();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmPendIntent);
    }

    public void delete(int id) {
        Log.d(TAG, "Deleting and setting next alarm.");

        mRepository.delete(id);
    }

    public void setAlarmInManager(Alarm alarm) {
        Log.d(TAG, "Creating next alarm in AlarmManager.");
        final Context context = getApplication().getApplicationContext();

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        time.set(Calendar.MINUTE, alarm.getMin());

        Log.d(TAG, "Creating alarm intents.");
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmPendIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent editAlarmIntent = new Intent(context, CreateAlarmActivity.class);
        PendingIntent editAlarmPendIntent = PendingIntent.getActivity(context, 0, editAlarmIntent, 0);

        //Assigns the time and intents to the alarm.
        Log.d(TAG, "Setting alarm properties.");
        AlarmManager.AlarmClockInfo alarmInfo = new AlarmManager.AlarmClockInfo(time.getTimeInMillis(), editAlarmPendIntent);

        //Creates and sets the alarm clock in the alarm manager service.
        Log.d(TAG, "Finalising creation.");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setAlarmClock(alarmInfo, alarmPendIntent);
    }
}
