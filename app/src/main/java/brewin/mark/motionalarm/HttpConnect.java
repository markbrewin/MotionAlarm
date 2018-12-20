package brewin.mark.motionalarm;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnect {
    private static final String TAG = "HttpConnect";

    static String jsonData = "";

    public String getJSONFromUrl(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection restConnection = (HttpURLConnection) u.openConnection();
            restConnection.setRequestMethod("GET");
            restConnection.setRequestProperty("Content-length", "0");
            restConnection.setUseCaches(false);
            restConnection.setAllowUserInteraction(false);
            restConnection.setConnectTimeout(10000);
            restConnection.setReadTimeout(10000);
            restConnection.connect();
            int status = restConnection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(restConnection.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();

                    try {
                        jsonData = sb.toString();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing data.");
                        e.printStackTrace();
                    }

                    return jsonData;
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Malformed URL.");
        } catch (IOException ex) {
            Log.e(TAG, "IO Exception");
        }
        return null;
    }
}
