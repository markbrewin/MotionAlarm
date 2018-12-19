package brewin.mark.motionalarm;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tbl_alarm", indices = {@Index(value = {"hour", "min"}, unique = true)})
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    private String name;

    @NonNull
    private int hour;

    @NonNull
    private int min;

    public Alarm(String name, int hour, int min) {
        this.name = name;
        this.hour = hour;
        this.min = min;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public String getStrHour() {
        return String.format("%02d", hour);
    }

    public String getStrMin() {
        return String.format("%02d", min);
    }
}
