package brewin.mark.motionalarm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
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

import java.util.Date;

public class WakeCheck {
    private static final String TAG = "WakeCheck";

    private Context context;

    private FusedLocationProviderClient mFusedLocationClient;

    private Date timeOriginal;
    private int triggerIteration;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private Location oldLocation;
    private Location newLocation;

    public WakeCheck(Context context) {
        Log.d(TAG, "New WakeCheck created");

        timeOriginal = new Date();
        triggerIteration = 0;

        this.context = context;

        getLocation();
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
}
