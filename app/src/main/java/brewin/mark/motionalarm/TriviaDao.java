package brewin.mark.motionalarm;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TriviaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Trivia trivia);

    @Query("DELETE FROM tbl_trivia")
    void deleteAll();

    @Query("SELECT * from tbl_trivia ORDER BY id DESC")
    LiveData<List<Trivia>> getAllTrivia();
}
