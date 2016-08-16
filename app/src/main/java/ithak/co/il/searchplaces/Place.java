package ithak.co.il.searchplaces;

/**
 * Created by ithak on 8/10/2016.
 */
public class Place {
    String name;        // the place name
    String address;     // the place address
    String latitude;    // place location
    String longitude;
    int sqlId;

    // CTOR
    public Place(String name, String address, String latitude, String longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name;
    }
}

