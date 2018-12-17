package brewin.mark.motionalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

//Activity used to display that the alarm has been triggered.
public class AlarmActivity extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Alarm activity triggered.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
    }

    //Ends the alarm activity and returns user to main activity.
    public void alarmEnd(View view) {
        Log.d(TAG, "Alarm turned off.");

        finish();
    }
}
