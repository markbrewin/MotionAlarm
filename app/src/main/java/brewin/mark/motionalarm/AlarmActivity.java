package brewin.mark.motionalarm;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.IOException;

//Activity used to display that the alarm has been triggered.
public class AlarmActivity extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";

    //Media player required to play the alarm sound.
    private MediaPlayer alarmMedPlyr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(TAG, "Alarm activity triggered.");

        alarmSound();
    }

    //Ends the alarm activity and returns user to main activity.
    public void alarmEnd(View view) {
        Log.d(TAG, "Alarm turned off.");

        //Stop the alarm sound and release the resources being used.
        alarmMedPlyr.stop();
        alarmMedPlyr.release();

        finish();
    }

    //Plays the alarm ringtone.
    private void alarmSound() {
        //Fetches the default alarm ringtone and create the media player for it.
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        //In case no default alarm ringtone is found.
        if(alert == null) {
            //Fetch the notification ringtone.
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //In case no default notification is found.
            if(alert == null) {
                //Fetch the default ringtone.
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }


        try {
            //Creates the alarm media player with required properties.
            alarmMedPlyr = new MediaPlayer();
            alarmMedPlyr.setAudioStreamType(AudioManager.STREAM_ALARM);
            alarmMedPlyr.setLooping(true);
            alarmMedPlyr.setDataSource(getApplicationContext(), alert);
            alarmMedPlyr.prepare();
            alarmMedPlyr.start();
        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(this, "Error playing alarm ringtone.", Toast.LENGTH_LONG).show();
        }
    }
}
