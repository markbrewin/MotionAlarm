package brewin.mark.motionalarm;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
import java.util.List;

public class AlarmRepository {
    private AlarmDao mAlarmDao;
    private LiveData<List<Alarm>> mAllAlarms;
    private LiveData<Alarm> curAlarm;
    private LiveData<Alarm> nextAlarm;

    AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getDatabase(application);
        mAlarmDao = db.alarmDao();
        mAllAlarms = mAlarmDao.getAllAlarms();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    LiveData<Alarm> getCurAlarm(Calendar time) {
        curAlarm = mAlarmDao.getCurAlarm(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        return curAlarm;
    }

    LiveData<Alarm> getNextAlarm() {
        nextAlarm = mAlarmDao.getNextAlarm(Calendar.getInstance().getTimeInMillis());
        return nextAlarm;
    }

    public void insert(Alarm alarm) {
        new insertAsyncTask(mAlarmDao).execute(alarm);
    }

    public void delete(Alarm alarm){
        new deleteAsyncTask(mAlarmDao).execute(alarm);
    }

    private static class insertAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao mAsyncTaskDao;

        insertAsyncTask(AlarmDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao mAsyncTaskDao;

        deleteAsyncTask(AlarmDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}
