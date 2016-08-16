package ithak.co.il.searchplaces;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class JSON_Service extends IntentService {

    //Activity callerActivity = null;
    final ArrayList<Place> placeList = new ArrayList<>(); // onPostExecute will fill the elements in the array list
    DbCommands dBCommands;
    static final private String GOOGLE_KEY = "AIzaSyA4M2fLr04IOPDtXnYDEdDhCVQ-3zuQhPE";

    public JSON_Service() {
        super("JSON_Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String cityStr = intent.getStringExtra("CityStr");
        String foodTypeStr = intent.getStringExtra("FoodTypeStr");
        // example
        // https://maps.googleapis.com/maps/api/place/textsearch/json?query=pizza%20in%20jerusaelm&key=AIzaSyA4M2fLr04IOPDtXnYDEdDhCVQ-3zuQhPE
        String webLink = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + foodTypeStr + "%20in%20" + cityStr + "&key=" + GOOGLE_KEY;

        // clear the DB for the new query
        dBCommands = new DbCommands(this);
        dBCommands.deleteAllPlaces();

        // Start a new JSON Search with a Tread.
        new JSON_PlaceRead(JSON_Service.this, cityStr, foodTypeStr);
        Toast.makeText(getApplicationContext(), "JSON Service", Toast.LENGTH_SHORT).show();
    }
    /*
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        //must close the reader
                        input.close();
                    } catch (IOException e) {
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
                    Toast.makeText(getApplicationContext(), "Pklace Not Found", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Query Finished with Success", Toast.LENGTH_SHORT).show();
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
    */
}
