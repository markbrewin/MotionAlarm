package brewin.mark.motionalarm;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.Calendar;
import java.util.List;

public class AlarmRepository {
    private AlarmDao mAlarmDao;
    private TriviaDao mTriviaDao;
    private LiveData<List<Alarm>> mAllAlarms;
    private LiveData<List<Trivia>> mAllTrivia;
    private LiveData<Alarm> curAlarm;
    private LiveData<Alarm> nextAlarm;

    AlarmRepository(Application application) {
        AlarmDatabase db = AlarmDatabase.getDatabase(application);
        mAlarmDao = db.alarmDao();
        mTriviaDao = db.triviaDao();
        mAllAlarms = mAlarmDao.getAllAlarms();
        mAllTrivia = mTriviaDao.getAllTrivia();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    LiveData<List<Trivia>> getAllTrivia() {
        return mAllTrivia;
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

    public void insertTrivia(Trivia trivia) {
        new insertTriviaAsyncTask(mTriviaDao).execute(trivia);
    }

    public void delete(Alarm alarm){
        new deleteAlarmAsyncTask(mAlarmDao).execute(alarm);
    }

    public void delete(int id){
        new deleteIdAsyncTask(mAlarmDao).execute(id);
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

    private static class insertTriviaAsyncTask extends AsyncTask<Trivia, Void, Void> {
        private TriviaDao mAsyncTaskDao;

        insertTriviaAsyncTask(TriviaDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Trivia... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAlarmAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao mAsyncTaskDao;

        deleteAlarmAsyncTask(AlarmDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Alarm... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class deleteIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        private AlarmDao mAsyncTaskDao;

        deleteIdAsyncTask(AlarmDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Integer... params)  {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}
