package brewin.mark.motionalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

@Entity(tableName = "tbl_alarm")
public class Alarm {
    private static final String TAG = "Alarm";

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    private String name;

    @NonNull
    private Calendar time;

    public Alarm(String name, @NonNull Calendar time) {
        this.name = name;
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getTime() {
        return time;
    }

    public String getHour() {
        return String.format("%02d", time.get(Calendar.HOUR_OF_DAY));
    }

    public String getMin() {
        return String.format("%02d", time.get(Calendar.MINUTE));
    }
}
