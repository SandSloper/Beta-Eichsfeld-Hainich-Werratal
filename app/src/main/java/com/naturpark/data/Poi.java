package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.location.Location;

import java.util.Random;

public class Poi {

    static public String[] RatingStrings = { "Unbekannt", "Ungeeigent", "Bedingt", "Geeignet" };

    private int _id;
    private int _type;
    Location _location;
    private String _name;
    private String _address;
    private String _rating;
    private String _info;

    public Poi(int id, int type, Location location, String name, String address,String rating,String info)
    {
        _id = id;
        _type = type;
        _location = location;
        _name = name;
        _address = address;
        _rating = rating;
        _info = info;
    }

    public int id() { return _id; }
    public int type() { return _type; }
    public Location location() { return _location; }
    public String name() { return _name; }
    public String address() { return _address; }
    public String rating() { return _rating; };
    public String info() { return _info; };

    public int rating_id() {

        for (int i = 0; i < RatingStrings.length; ++i) {
            if (RatingStrings[i].equals(_rating)) {
                return i;
            }
        }

        return 0;
    }
}
