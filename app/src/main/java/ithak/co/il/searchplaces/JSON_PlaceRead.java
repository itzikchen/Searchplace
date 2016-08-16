package ithak.co.il.searchplaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by ithak on 8/10/2016.
 */
public class JSON_PlaceRead {
    Context callerContext = null;
    final ArrayList<Place> placeList = new ArrayList<>(); // onPostExecute will fill the elements in the array list
    DbCommands dBCommands;
    static final private String GOOGLE_KEY =  "AIzaSyA4M2fLr04IOPDtXnYDEdDhCVQ-3zuQhPE";

    // this function is called by
    //   1. JSON_Service ( that extent context )
    //   2. MainActivity ( that extend Activity )
    //   Context is extend both by Activity and by Service - so get the caller Context.
    public JSON_PlaceRead (Context callerContext, String mainCityStr, String mainFoodTypeStr) {

        this.callerContext = callerContext;
        // search with UTF-8 so it would be posible to put two words in the search.
        String mainCityUtf8 = "";
        String mainFoodTypeUtf8 = "";
        try {
            mainCityUtf8 = URLEncoder.encode(mainCityStr, "utf-8");
            mainFoodTypeUtf8 = URLEncoder.encode(mainFoodTypeStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LocalBroadcastMessageSend(false,"");
            e.printStackTrace();
        }

        String googleKey = "AIzaSyA4M2fLr04IOPDtXnYDEdDhCVQ-3zuQhPE";

        // example
        // https://maps.googleapis.com/maps/api/place/textsearch/json?query=pizza%20in%20jerusaelm&key=AIzaSyA4M2fLr04IOPDtXnYDEdDhCVQ-3zuQhPE
        String webLink = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + mainFoodTypeUtf8 + "%20in%20" + mainCityUtf8 + "&key=" + googleKey;
        // create a new DownloadWebsite object
        new DownloadWebsite().execute(webLink);
        dBCommands = new DbCommands(callerContext);
        // clear the DB for the new query
        dBCommands.deleteAllPlaces();
    }

    // background task to download the JSON file.
    public class DownloadWebsite extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            //start download....
            int lineConut = 0;

            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try {
                //create a url:
                URL url = new URL(params[0]);
                //create a connection and open it:
                connection = (HttpURLConnection) url.openConnection();

                //status check:
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    //connection not good - return.
                }

                //get a buffer reader to read the data stream as characters(letters)
                //in a buffered way.
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                //go over the input, line by line
                String line = "";
                while ((line = input.readLine()) != null) {
                    //append it to a StringBuilder to hold the
                    //resulting string
                    response.append(line + "\n");
                    lineConut++;
                }
            } catch (MalformedURLException e) {
                LocalBroadcastMessageSend(false,"");
                e.printStackTrace();
            } catch (IOException e) {
                LocalBroadcastMessageSend(false, "no internet connection");
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        //must close the reader
                        input.close();
                    } catch (IOException e) {
                        LocalBroadcastMessageSend(false,"");
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    //must disconnect the connection
                    connection.disconnect();
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String resutFromWebsite) {
            //the main JSON object - initialize with string
            try {
                JSONObject mainObject = new JSONObject(resutFromWebsite);
                // extract the JSON array that exist under "Search" name
                JSONArray myArray = mainObject.getJSONArray("results");
                if (myArray == null) {
                    Log.d("onPost", "problem with GSOM");
                    Toast.makeText(callerContext, "Place not found", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // go over the JSON results
                        for (int i = 0; i < myArray.length(); i++) {
                            //inner objects inside the array
                            JSONObject innerObj = myArray.getJSONObject(i);
                            //JSONObject locationObj = myArray.getJSONObject("location");
                            String name = innerObj.getString("name");
                            String address = innerObj.getString("formatted_address");

                            JSONObject geometricObj = innerObj.getJSONObject("geometry");
                            JSONObject locationObj = geometricObj.getJSONObject("location");
                            String lat = locationObj.getString("lat");
                            String lng = locationObj.getString("lng");
                            Place place = new Place(name, address, lat, lng);
                            placeList.add(place);
                            // Add the location to the database.
                            dBCommands.addPlace(place);
                        }
                        dumpMovieList (placeList);
                        Toast.makeText(callerContext, "JSON Tread :: Query Finished with Success", Toast.LENGTH_SHORT).show();
                        LocalBroadcastMessageSend(true,"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //myYnetTV.setText(""+values[0]);
        }

        void dumpMovieList (ArrayList<Place> movieWeb ) {
            for (Place item: movieWeb) {
                Log.d("Movie", item.name);
            }
        }
    }

    // send a local broadcast message that the search is done
    public void LocalBroadcastMessageSend (boolean isSuccess, String message) {
        // send a local Braodcast message that the search is done with fail
        Intent broadcastMessage = new Intent("broadcast.local.com.localbroadcast.JSON_SEARCH_IS_DONE");
        String str = isSuccess ? "true":"false";
        broadcastMessage.putExtra("Success",str );
        broadcastMessage.putExtra("Message",message );
        LocalBroadcastManager.getInstance(callerContext).sendBroadcast(broadcastMessage);
    }
}

