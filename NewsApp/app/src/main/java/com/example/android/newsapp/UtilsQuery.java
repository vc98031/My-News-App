package com.example.android.newsapp;

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

/**
 * Created by Mirka on 08/07/2017.
 */

public class UtilsQuery {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = UtilsQuery.class.getSimpleName();
    private static String title;
    private static String category;
    private static String url;

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private UtilsQuery() {
    }

    /**
     * Return list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> fetchNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {

        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<News> newses = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return newses;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            // If the request was successful (response code 200)
            // then read the input stream and parse the response
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Response code of the object: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON result: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response
     * from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
        }
        return result.toString();
    }

    public static ArrayList<News> extractFeatureFromJson(String jsonResponse) {

        // Create an empty ArrayList that we can start adding newses to
        ArrayList<News> newses = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Convert JSONObject from the JSON response string
            JSONObject news_json_response = new JSONObject(jsonResponse);

            // Extract "items" JSONArray associated with the key called "items"
            // which represents a list of information about the book
            if (news_json_response.has("response")) {
                JSONObject response = news_json_response.getJSONObject("response");
                if (response.has("results")) {
                    JSONArray resultsArray = response.getJSONArray("results");

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject resultDetails = resultsArray.getJSONObject(i);
                        if (resultDetails.has("webTitle")) {
                            title = resultDetails.getString("webTitle");

                        }
                        if (resultDetails.has("sectionName")) {
                            category = resultDetails.getString("sectionName");
                        }

                        if (resultDetails.has("webUrl")) {
                            url = resultDetails.getString("webUrl");
                        }
                        if (resultDetails.has("tags")) {
                            JSONArray tagsArray = resultDetails.getJSONArray("tags");
                            if (tagsArray.length() > 0) {
                                JSONObject tagsDetails = tagsArray.getJSONObject(0);
                                if (tagsDetails.has("webTitle")) {
                                    newses.add(new News(title, category, url));
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the JSON list books", e);
        }

        // Return the list of books
        return newses;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            //handle exception
        }
        return url;
    }
}

