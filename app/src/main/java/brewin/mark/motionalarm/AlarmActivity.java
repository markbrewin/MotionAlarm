package brewin.mark.motionalarm;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

//Activity used to display that the alarm has been triggered.
public class AlarmActivity extends AppCompatActivity {
    private static final String TAG = "AlarmActivity";

    public double backupMins = 1;

    //Media player required to play the alarm sound.
    private MediaPlayer alarmMedPlyr;

    private WakeCheck wakeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(TAG, "Alarm activity triggered.");

        wakeCheck = new WakeCheck(this);

        TextView alarmTime = findViewById(R.id.txtAlarmTime);
        alarmTime.setText(getString(R.string.alarm_time, wakeCheck.getTimeOriginal('h'), wakeCheck.getTimeOriginal('m')));

        alarmSound();
    }

    //Ends the alarm activity and schedules the backup alarm.
    public void alarmStop(View view) {
        Log.d(TAG, "Alarm turned off.");

        //Stop the alarm sound and release the resources being used.
        alarmMedPlyr.stop();
        alarmMedPlyr.release();

        startBackupCountdown();
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

    private void startBackupCountdown() {
        Log.d(TAG, "Backup alarm countdown started.");

        double countdownTime = (backupMins * 60) * 1000;

        new CountDownTimer((long) countdownTime, 1000) {
            TextView backupCountdown = findViewById(R.id.txtBackupCountdown);

            public void onTick(long millisUntilFinished) {
                String mins = String.format("%02d", (millisUntilFinished / 1000) / 60);
                String secs = String.format("%02d", (millisUntilFinished / 1000) % 60);

                backupCountdown.setText(getString(R.string.alarm_time, mins, secs));
            }

            public void onFinish() {
                Log.d(TAG, "Backup alarm triggered.");

                backupCountdown.setText(getString(R.string.alarm_time, "00", "00"));

                wakeCheck.checkMovement();
            }
        }.start();
    }
}
