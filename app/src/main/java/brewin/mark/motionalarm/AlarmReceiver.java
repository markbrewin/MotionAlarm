package brewin.mark.motionalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

//Listens for an alarm to be triggered at its set time.
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "Alarm Triggered");

        //Create a new activity for the alarm.
        Intent alarmActivity = new Intent();
        alarmActivity.setClassName("brewin.mark.motionalarm", "brewin.mark.motionalarm.AlarmActivity");
        alarmActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Start the activity.
        context.startActivity(alarmActivity);
    }
}
