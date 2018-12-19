package brewin.mark.motionalarm;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AlarmDao {
    @Insert
    void insert(Alarm alarm);

    @Query("DELETE FROM tbl_alarm")
    void deleteAll();

    @Query("SELECT * from tbl_alarm ORDER BY id")
    LiveData<List<Alarm>> getAllAlarms();
}
