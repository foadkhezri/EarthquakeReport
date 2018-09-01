package foadkhezri.github.com.earthquakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

 final class QueryUtils {

    private QueryUtils() {
    }

    public static ArrayList<Earthquake> fetchEarthquakeData (String requestUrl) {
             /*try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        if (requestUrl == null) {
            return null;
        }

        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            assert url != null;
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
        return extractFeatureFromJson(jsonResponse);
    }
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
        ArrayList<Earthquake> earthQuakes = new ArrayList<>();
        try {
            JSONObject baseJsonObject = new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJsonObject.getJSONArray("features");
            for (int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");
                Double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");
                if (place.contains("Iraq") || place.contains("Pakistan") || place.contains("Azerbaijan") || place.contains("Turkey") || place.contains("Turkmenistan") || place.contains("Afghanistan") || place.contains("Armenia"))
                    continue;
                Long time = properties.getLong("time");
                String url = properties.getString("url");
                Earthquake earthQuake = new Earthquake(magnitude, place, time, url);
                earthQuakes.add(earthQuake);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return earthQuakes;
    }
}
