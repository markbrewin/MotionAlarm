package brewin.mark.motionalarm;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;

import java.util.Calendar;

public class WakeCheck {
    private static final String TAG = "WakeCheck";

    private Calendar alarmTimeOriginal;
    private int triggerIteration;

    public WakeCheck() {
        Log.d(TAG, "New WakeCheck created");

        alarmTimeOriginal = Calendar.getInstance();
        triggerIteration = 0;
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

    public boolean checkMovement(Location oldLocation, Location newLocation, double wakeDistance) {
        triggerIteration++;

        if(oldLocation == null) {
            Log.d(TAG, "Not enough data to compare values.");

            return false;
        } else {
            Log.d(TAG, "Checking user has woken up.");

            double dist = distance(oldLocation, newLocation);
            Log.d(TAG, "Distance phone has travelled: " + dist);

            if(dist < wakeDistance) {
                Log.d(TAG, "Phone not moved required distance. Assume user is still asleep.");

                return false;
            } else {
                Log.d(TAG, "Phone moved, user awake and deactivating alarm.");

                return true;
            }
        }
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
