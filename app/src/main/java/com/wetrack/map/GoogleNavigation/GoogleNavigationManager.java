package com.wetrack.map.GoogleNavigation;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.R;
import com.wetrack.utils.ConstantValues;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by moziliang on 16/10/15.
 */
public class GoogleNavigationManager {
    private static GoogleNavigationManager mGoogleNavigationManager = null;
    public static GoogleNavigationManager getInstance(Context context) {
        if (mGoogleNavigationManager == null) {
            mGoogleNavigationManager = new GoogleNavigationManager(context);
        }
        return mGoogleNavigationManager;
    }

    public void setmGoogleNavigationResultListener(GoogleNavigationResultListener mGoogleNavigationResultListener) {
        this.mGoogleNavigationResultListener = mGoogleNavigationResultListener;
    }

    private GoogleNavigationResultListener mGoogleNavigationResultListener = null;

    private Context mContext;

    private GoogleNavigationManager(Context context) {
        mContext = context;
    }

    public void getResultFromGoogle(GoogleNavigationFormat googleNavigationData) {
        String url = encodeGoogleNavigationFormatToUrl(googleNavigationData);

        (new GetResultFromGoogleThread(url)).start();
    }

    private String encodeGoogleNavigationFormatToUrl(GoogleNavigationFormat googleNavigationData) {
        LatLng fromPosition = googleNavigationData.origin;
        LatLng toPosition = googleNavigationData.destination;
        return String.format(Locale.ENGLISH,
                "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                fromPosition.latitude, fromPosition.longitude,
                toPosition.latitude, toPosition.longitude,
                mContext.getResources().getString(R.string.google_direction_key));
    }

    private ArrayList<LatLng> decodeGoogleNavigationResult(String resultString) {
        ArrayList<LatLng> answerPath = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(resultString);

            JSONObject startLocation = obj.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0)
                    .getJSONObject("start_location");
            answerPath.add(new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng")));

            String overviewPolylineString = obj.getJSONArray("routes").getJSONObject(0)
                    .getJSONObject("overview_polyline").getString("points");

            answerPath.addAll(decodeOverviewPolyline(overviewPolylineString));

            JSONObject endLocation = obj.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0)
                    .getJSONObject("end_location");
            answerPath.add(new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng")));

//            JSONObject northeastBound = obj.getJSONArray("routes").getJSONObject(0)
//                    .getJSONObject("bounds").getJSONObject("northeast");
//            LatLng northestPosition = new LatLng(northeastBound.getDouble("lat"), northeastBound.getDouble("lng"));

//            JSONObject southwestBound = obj.getJSONArray("routes").getJSONObject(0)
//                    .getJSONObject("bounds").getJSONObject("southwest");
//            LatLng southwestPosition = new LatLng(southwestBound.getDouble("lat"), southwestBound.getDouble("lng"));
            return answerPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<LatLng> decodeOverviewPolyline(String overviewPolylineString) {

        ArrayList<LatLng> answer = new ArrayList<LatLng>();
        int index = 0, len = overviewPolylineString.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = overviewPolylineString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = overviewPolylineString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            answer.add(new LatLng(lat / 100000d, lng / 100000d));
        }

        return answer;
    }

    private class GetResultFromGoogleThread extends Thread {

        private String url;

        public GetResultFromGoogleThread(String url) {
            this.url = url;
        }

        public void run() {
            String USER_AGENT = "Mozilla/5.0";
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);

                int responseCode = con.getResponseCode();
                Log.d(ConstantValues.debugTab, "\nSending 'GET' request to URL : " + url);
                Log.d(ConstantValues.debugTab, "Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                String resultString = response.toString();
                Log.d(ConstantValues.debugTab, "get result:" + resultString);

                ArrayList<LatLng> result = decodeGoogleNavigationResult(resultString);

                if (mGoogleNavigationResultListener != null && result != null) {
                    mGoogleNavigationResultListener.onReceiveResult(result);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
