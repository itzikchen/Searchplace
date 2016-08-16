package ithak.co.il.searchplaces;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    ArrayList<Place> placeList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    // CTOR
    public ListFragment() {
        // Required empty public constructor
        Log.e("ListPrag", "CTOR");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(Place place) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, place.name);
        args.putString(ARG_PARAM2, place.address);
        args.putString(ARG_PARAM3, place.latitude);
        args.putString(ARG_PARAM4, place.longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ListPrag", "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // bind the fragment with the xml
        View v = inflater.inflate(R.layout.fragment_list, container, false); // fragment_list: xml fragment name.

        ListView lv = (ListView) v.findViewById(R.id.listView);
        MySqlOpenHelper helper = new MySqlOpenHelper(getActivity());

        final Cursor c = helper.getReadableDatabase().query(DBConstants.tableName, null,null,null,null,null,null);

        //create an adapter
        MyPlacesAdapter adapter = new MyPlacesAdapter(getActivity(), c);

        lv.setAdapter(adapter);

        // short click on list view.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // short click on a List View, go to Item Edit Activity.
                Toast.makeText( getActivity(), "open Map", Toast.LENGTH_SHORT ).show();

                // get the record
                if(c.moveToPosition(position)) {
                    Place place = null;
                    // get the Place from the DB.
                    DbCommands dBCommands;
                    int SQLiD = c.getInt(c.getColumnIndex("_id"));
                    dBCommands = new DbCommands(getActivity());
                    Cursor resultCursor = dBCommands.getDataFromDBAsCursor(SQLiD);
                    if(resultCursor.moveToNext())
                    {
                        place = new Place(
                        resultCursor.getString(resultCursor.getColumnIndex(DBConstants.placeName)),
                        resultCursor.getString(resultCursor.getColumnIndex(DBConstants.placeAddress)),
                        resultCursor.getString(resultCursor.getColumnIndex(DBConstants.placeLatitude)),
                        resultCursor.getString(resultCursor.getColumnIndex(DBConstants.placeLongitude)));
                    }

                    // upddate Main about the new
                    try {
                        PlaceFragmentChanger activityThatImplenetsFoodFragmentChanger = (PlaceFragmentChanger) getActivity();
                        activityThatImplenetsFoodFragmentChanger.changeFragments(place);
                    } catch (ClassCastException ee) {
                        Log.d("Foods", "The host Activity of ListFragment must Implement PlaceFragmentChanger");
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return v;
    }
    // save the class variable before for rotation.
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("ListPrag", "Rotate save");
        //outState.putInt("userTry", userTry);
    }
    // restore the class after screen rotate.
    public void restoreOnScreenRotate (Bundle savedInstanceState) {
        Log.e("ListPrag", "rotate restore");
    }

    private void placeDBReaed(ArrayList<Place> placeList) {

    }
}
