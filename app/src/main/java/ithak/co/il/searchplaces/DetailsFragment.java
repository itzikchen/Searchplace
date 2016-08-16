package ithak.co.il.searchplaces;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailsFragment extends Fragment {

    Place currentLocation;

    public DetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(Place selectedLocation ) {
        DetailsFragment fragment = new DetailsFragment();
        fragment.currentLocation= selectedLocation;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        TextView FoodTitleTV =(TextView) v.findViewById(R.id.foodNameTV);
        TextView locationTV =(TextView) v.findViewById(R.id.locationTV);

        FoodTitleTV.setText(currentLocation.name);
        locationTV.setText(currentLocation.address);

        FragmentManager manager= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            manager = getChildFragmentManager();
        }  else {
            manager=getFragmentManager();
        }
        MapFragment mapFragment = (MapFragment) manager.findFragmentById(R.id.mymapFragmnet);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                double latitude = Double.parseDouble(currentLocation.latitude);
                double longitude = Double.parseDouble(currentLocation.longitude);

                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // setup map position and zoom
                LatLng position = new LatLng(latitude, longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(position, 15);
                // add marker
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Pizza"));

                googleMap.moveCamera(update);
            }
        });
        return v;
    }
}
