package brewin.mark.motionalarm;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("DELETE FROM tbl_alarm")
    void deleteAll();

    @Query("SELECT * from tbl_alarm ORDER BY hour, min")
    LiveData<List<Alarm>> getAllAlarms();

    @Query("SELECT * FROM tbl_alarm WHERE hour LIKE :hour AND min LIKE :minute LIMIT 1")
    LiveData<Alarm> getCurAlarm(int hour, int minute);

    @Query("SELECT * FROM tbl_alarm ORDER BY (time - :curTime) LIMIT 1")
    LiveData<Alarm> getNextAlarm(long curTime);
}
