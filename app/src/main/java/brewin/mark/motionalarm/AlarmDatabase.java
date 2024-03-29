package brewin.mark.motionalarm;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Alarm.class, Trivia.class}, version = 5)
@TypeConverters({Converters.class})
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
    public abstract TriviaDao triviaDao();

    private static volatile AlarmDatabase INSTANCE;

    static AlarmDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (AlarmDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AlarmDatabase.class, "alarm_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AlarmDao aDao;
        private final TriviaDao tDao;

        PopulateDbAsync(AlarmDatabase db) {
            aDao = db.alarmDao();
            tDao = db.triviaDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            aDao.deleteAll();
            tDao.deleteAll();

            /*Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5);

            Alarm alarm = new Alarm("Hello there!", cal);
            mDao.insert(alarm);*/

            return null;
        }
    }
}
