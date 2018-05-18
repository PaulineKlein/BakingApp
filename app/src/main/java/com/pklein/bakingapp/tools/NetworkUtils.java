package com.pklein.bakingapp.tools;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_LIST_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    /**
     * Builds the URL used to get the baking.json
     * this URL will return all the informations used to display resident baker-in-chief recipes
     *
     * @return The URL.
     */
    public static URL buildListUrl() {
        Uri builtUri = Uri.parse(BASE_LIST_URL).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * This method allows to know whether the phone is connected to internet or not
     *
     * @param cm keep informations of phone connections
     * @return returns true if the phone is connected to internet, false otherwhise
     */
    public static boolean isconnected(ConnectivityManager cm){

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean phoneConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return phoneConnected;
    }

    /**
     * This method gives the URL used by Youtube to watch a Trailer
     *
     * @param videoURL  The String URL that identify the video
     * @return URL of the video to launch or NULL if the String is empty
     */
    public static Uri getvideoURI(String videoURL){
        if(!videoURL.equals("")){
            return Uri.parse(videoURL);
        }
        else
            return null;
    }
}
