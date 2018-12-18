package brewin.mark.motionalarm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class WakeCheck {

    private static final String TAG = "WakeCheck";

    private Context context;

    private FusedLocationProviderClient mFusedLocationClient;

    private Date timeOriginal;
    private int triggerIteration;

    private double lastLat;
    private double lastLon;
    private double lastAlt;

    private double newLat;
    private double newLon;
    private double newAlt;

    public WakeCheck(Context context) {
        Log.d(TAG, "New WakeCheck created");

        timeOriginal = new Date();

        this.context = context;

        getLocation();
    }

    private void getLocation() {
        Log.d(TAG, "Fetching phone's location.");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission granted.");

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                lastAlt = newAlt;
                                lastLat = newLat;
                                lastLon = newLon;

                                newAlt = location.getAltitude();
                                newLat = location.getLatitude();
                                newLon = location.getLongitude();

                                Log.d(TAG, "Phone Location retrieved.");
                                Log.d(TAG, "Old Location - Alt: " + lastAlt + "\tLat: " + lastLat + "\tLon: " + lastLon);
                                Log.d(TAG, "New Location - Alt: " + newAlt + "\tLat: " + newLat + "\tLon: " + newLon);
                            }
                        }
                    });
        } else {
            Log.d(TAG, "Permissions not granted to access location!");
        }
    }
}
