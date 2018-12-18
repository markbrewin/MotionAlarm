package brewin.mark.motionalarm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class WakeCheck {
    private static final String TAG = "WakeCheck";

    private Context context;

    private FusedLocationProviderClient mFusedLocationClient;

    private Calendar alarmTimeOriginal;
    private int triggerIteration;

    private int wakeDistance;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location oldLocation;
    private Location newLocation;

    public WakeCheck(Context context, int wakeDistance) {
        Log.d(TAG, "New WakeCheck created");

        alarmTimeOriginal = Calendar.getInstance();
        triggerIteration = 0;

        this.context = context;
        this.wakeDistance = wakeDistance;

        getLocation();
    }

    public String getTimeOriginal(char cat) {
        String val = "";

        switch (cat) {
            case 'h':
                val = String.format("%02d", alarmTimeOriginal.get(Calendar.HOUR_OF_DAY));
                break;
            case 'm':
                val = String.format("%02d", alarmTimeOriginal.get(Calendar.MINUTE));
                break;
        }

        return val;
    }

    public int getTriggerIteration() {
        return triggerIteration;
    }

    public boolean checkMovement() {
        triggerIteration++;

        getLocation();

        if(oldLocation == null) {
            Log.d(TAG, "Not enough data to compare values.");

            return false;
        } else {
            Log.d(TAG, "Checking user has woken up.");

            if(distance(oldLocation, newLocation) < wakeDistance) {
                Log.d(TAG, "Phone not moved required distance. Assume user is still asleep.");

                return false;
            } else {
                Log.d(TAG, "Phone moved, user awake and deactivating alarm.");

                return true;
            }
        }
    }

    private void getLocation() {
        Log.d(TAG, "Fetching phone's location.");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) {
                    Log.d(TAG, "No locationResult retrieved.");
                } else {
                    Log.d(TAG, "Location retrieved.");

                    oldLocation = newLocation;
                    newLocation = locationResult.getLastLocation();

                    //Log.d(TAG, "Old Location - Alt: " + oldLocation.getAltitude() + "\tLat: " +  oldLocation.getLatitude() + "\tLon: " + oldLocation.getLongitude());
                    Log.d(TAG, "New Location - Alt: " + newLocation.getAltitude() + "\tLat: " +  newLocation.getLatitude() + "\tLon: " + newLocation.getLongitude());
                }
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "Requesting location updates.");

                if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted.");

                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                } else {
                    Log.d(TAG, "Permissions not granted to access location!");
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to fetch location.");
            }
        });
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(Location loc1, Location loc2) {
        double lat1 = loc1.getLatitude();
        double lat2 = loc2.getLatitude();
        double lon1 = loc1.getLongitude();
        double lon2 = loc2.getLongitude();
        double alt1 = loc1.getAltitude();
        double alt2 = loc2.getAltitude();

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = alt1 - alt2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
