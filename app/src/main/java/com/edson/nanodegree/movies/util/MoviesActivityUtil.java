package com.edson.nanodegree.movies.util;

import android.util.Log;

import com.edson.nanodegree.movies.bean.CategoryBean;
import com.edson.nanodegree.movies.bean.MovieBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edson on 21/07/2017.
 */

public class MoviesActivityUtil {

    public static final String LOG_TAG = MoviesActivityUtil.class.getSimpleName();

    public static Map<String, Integer> getMapResult(String jsonResult) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonResult);
        JSONArray array = jsonObject.getJSONArray("genres");
        Map<String, Integer> mapResult = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jObj = array.getJSONObject(i);
            String name = jObj.getString("name");
            Integer id = jObj.getInt("id");
            mapResult.put(name, id);
        }
        return mapResult;
    }

    public static String getJsonResultURL(String myUrl) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonStr = null;

        try {
            URL url = new URL(myUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonStr;
    }

    public static CategoryBean loadMoviesFromDiscoveryJson(String json, CategoryBean category) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray array = jsonObject.getJSONArray("results");
            StringBuilder sb = new StringBuilder();
            sb.append(" IDs: ");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jObj = array.getJSONObject(i);
                String path = jObj.getString("poster_path");
                if (path != null && !path.equals("null")) {
                    int id = jObj.getInt("id");
                    String title = jObj.getString("original_title");
                    Double voteAverage = jObj.getDouble("vote_average");
                    Float rating = voteAverage == null ? 0.0f : voteAverage.floatValue();
                    String synopsis = jObj.getString("overview");
                    String releaseDate = jObj.getString("release_date");
                    sb.append(id + ", ");
                    String pathUrl = path;
                    category.addMovie(new MovieBean(id, pathUrl, title, synopsis, rating, releaseDate));
                }
            }
            Log.i(LOG_TAG, sb.toString());
            return category;
        } catch (JSONException e) {
            Log.i(LOG_TAG, "Error al cargar las videos");
            return null;
        }
    }

}
