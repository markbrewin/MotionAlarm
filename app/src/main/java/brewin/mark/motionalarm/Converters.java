package brewin.mark.motionalarm;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;

public class Converters {
    @TypeConverter
    public static Calendar fromTimestamp(Long value) {
        if (value == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(value);
            return cal;
        }
    }

    @TypeConverter
    public static Long timeToTimestamp(Calendar date) {
        return date == null ? null : date.getTimeInMillis();
    }
}