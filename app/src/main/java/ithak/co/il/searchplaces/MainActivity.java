package ithak.co.il.searchplaces;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements PlaceFragmentChanger {
    DbCommands dBCommands;
    ListFragment listFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dBCommands = new DbCommands(MainActivity.this);

        // create the list fragment
        // this fragment is not declared in the XML, it created dynamically.
        FragmentManager manager = getFragmentManager();
        ListFragment listFragment = ListFragment.newInstance(new Place("1","2","3","4")); // pass data to the fragment
        listFragment.onSaveInstanceState(savedInstanceState); // restore on screen rotate.
        FragmentTransaction transaction = manager.beginTransaction();
        //transaction.addToBackStack("added shoe " + index); // make a list so we can return back by using popBackStack()
        transaction.replace(R.id.mapFragmentContianer , listFragment).commit(); // replace the fragments.

        // Listen on the Local Broadcast - catching the event from the JSON search when search is done.
        IntentFilter filter = new IntentFilter("broadcast.local.com.localbroadcast.JSON_SEARCH_IS_DONE");
        JSONSearchIsDoneReciever reciever = new JSONSearchIsDoneReciever();
        LocalBroadcastManager.getInstance(this).registerReceiver(  reciever , filter);

        // Search the Google API for places - that fill the DB with new places.
        ((Button) findViewById(R.id.MainGetBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFreez(false);
                EditText mainCityET = (EditText) findViewById(R.id.MainCityET);
                EditText mainFoodTypeET = (EditText) findViewById(R.id.MainFoodTypeET);

                String mainCityStr = mainCityET.getText().toString();
                String mainFoodTypeStr = mainFoodTypeET.getText().toString();
                // direct call for the JSON Search that open a thread for the search.
                new JSON_PlaceRead(MainActivity.this, mainCityStr, mainFoodTypeStr);
            }
        });

        // refresh the ListView with the content from the DB.
        ((Button) findViewById(R.id.MainRefreshBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            refreshList();
            }
        });

        // fill dummy data for places.
        ((Button) findViewById(R.id.MainFillDummyDataBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            DbCommands dBCommands = new DbCommands(MainActivity.this);
            dBCommands.deleteAllPlaces();
            fillDummyData();
            }
        });
        // start the JSON search with a service - that fill the DB with places.
        ((Button) findViewById(R.id.MainFillServiceStartBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonFreez(false);
                EditText mainCityET = (EditText) findViewById(R.id.MainCityET);
                EditText mainFoodTypeET = (EditText) findViewById(R.id.MainFoodTypeET);

                String mainCityStr = mainCityET.getText().toString();
                String mainFoodTypeStr = mainFoodTypeET.getText().toString();

                // start the service
                Intent serviceIntent = new Intent(MainActivity.this, JSON_Service.class);
                serviceIntent.putExtra("CityStr", mainCityStr);         // put extra fields on the Service intent.
                serviceIntent.putExtra("FoodTypeStr", mainFoodTypeStr);
                startService(serviceIntent);
            }
        });
    }

    // fill dummy records in the DB - just for texting.
    private void fillDummyData() {
        Place place1 = new Place("ithak","chen","32.0","34.0");
        Place place2 = new Place("boni","the biy","32.5","34.8");
        dBCommands.addPlace(place1);
        dBCommands.addPlace(place2);
        refreshList();
    }

    // Listen on the Local Braodcast from JSON Search.
    class JSONSearchIsDoneReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("Success") == "true") {
                Toast.makeText(MainActivity.this, "Success - JSON Search is done.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Fail - JSON Search is done.", Toast.LENGTH_SHORT).show();
            }
            refreshList();
            buttonFreez(true);
        }
    }

    // this function is called upon GSOM thread finish retreiving all data.
    // GSON thread is started after creating the object JSON_PlaceRead
    public void JSON_PlaceUpdate (Place place) {
        /*
        EditText nameET = (EditText) findViewById(R.id.addEditNameET);
        EditText summeryET = (EditText) findViewById(R.id.addEditSummeryET);
        ImageView movieImageIV = (ImageView) findViewById(R.id.addEditImageIV);
        CheckBox seenCB = (CheckBox) findViewById(R.id.addEditSeenCB);
        RatingBar ratingRB = (RatingBar) findViewById(R.id.addEditRatingRT);

        showImageFromWeb(movieWeb.name, movieWeb.imagePtr, movieImageIV);

        nameET.setText(movieWeb.name);
        summeryET.setText(movieWeb.summery);
        seenCB.setChecked(movieWeb.isSeen == 1);
        ratingRB.setRating((float)movieWeb.rating);
        */
    }

    // refresh the listView to read again from the DB, both Main ListView and Fragment ListView
    private void refreshList() {

        ListView lv = (ListView) findViewById(R.id.MainPlacesLV);
        MySqlOpenHelper helper = new MySqlOpenHelper(this);

        final Cursor c = helper.getReadableDatabase().query(DBConstants.tableName, null,null,null,null,null,null);

        //create an adapter
        MyPlacesAdapter adapter = new MyPlacesAdapter(this, c);

        lv.setAdapter(adapter);
        // short click on list view.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // short click on a List View, go to Item Edit Activity.
            Toast.makeText( MainActivity.this, "Places Edit", Toast.LENGTH_SHORT ).show();

            if(c.moveToPosition(position))
            {
                int dbID = c.getInt(c.getColumnIndex("_id"));
            }
            }
        });

        // refresh the Fragment List as well:
        //transaction.replace(R.id.shoeFragmentContianer , listFragment).commit(); // replace the fragments.
        Place place = new Place("1","2","3","4");
        //changeFragments (place);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ListFragment detailsFragment = ListFragment.newInstance(place);
        transaction.addToBackStack("add map "+ place.name);
        transaction.replace(R.id.mapFragmentContianer, detailsFragment).commit();
    }

    // this function is called from the list fragment.
    // list fragment interact with maps Fragment via this function, and pass the resource to display.
    public  void changeFragments(final Place place)
    {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        DetailsFragment detailsFragment = DetailsFragment.newInstance(place);

        transaction.addToBackStack("add map "+ place.name);
        transaction.replace(R.id.mapFragmentContianer, detailsFragment).commit();

        /*
        FragmentManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            manager = getChildFragmentManager();
        }  else {
            manager = getFragmentManager();
        }
        manager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.mapFragmentContianer);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                // setup map position and zoom
                LatLng position = new LatLng(Double.parseDouble(place.latitude), Double.parseDouble(place.longitude));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 15);
                googleMap.moveCamera(update);
            }
        });
        */
    }

    // when setting a query, dont let other query to be sent
    private void buttonFreez (boolean isClickable) {
        Button MainGetBTN = (Button) findViewById(R.id.MainGetBTN);
        Button MainRefreshBTN = (Button) findViewById(R.id.MainRefreshBTN);
        Button MainFillDummyDataBTN = (Button) findViewById(R.id.MainFillDummyDataBTN);
        Button MainFillServiceStartBTN = (Button) findViewById(R.id.MainFillServiceStartBTN);

        MainGetBTN.setBackgroundColor( isClickable ? 0xFFc5e9e6 : 0xFF98b7b5);
        MainRefreshBTN.setBackgroundColor( isClickable ? 0xFFc5e9e6 : 0xFF98b7b5);
        MainFillDummyDataBTN.setBackgroundColor( isClickable ? 0xFFc5e9e6 : 0xFF98b7b5);
        MainFillServiceStartBTN.setBackgroundColor( isClickable ? 0xFFc5e9e6 : 0xFF98b7b5);

        MainGetBTN.setClickable(isClickable);
        MainRefreshBTN.setClickable(isClickable);
        MainFillDummyDataBTN.setClickable(isClickable);
        MainFillServiceStartBTN.setClickable(isClickable);
    }

    public boolean isPortrait()
    {
        boolean isPortrait = false;

        // if Fragmnet Continer is Found- Landscape
        TextView portraitTV = (TextView) findViewById(R.id.portraitTV);

        if(portraitTV == null) {
            isPortrait = true;
        }
        return  isPortrait;
    }
}
