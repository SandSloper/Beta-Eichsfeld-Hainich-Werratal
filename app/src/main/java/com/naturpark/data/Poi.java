package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.location.Location;

public class Poi {

    public Poi(int type, Location location, String name, String address, int classification)
    {
        _type = type;
        _location = location;
        _name = name;
        _address = address;
        _classification = classification;
    }

    public int type() { return _type; }
    public Location location() { return _location; }
    public String name() { return _name; }
    public String address() { return _address; }
    public int classification() { return _classification; }

    private int _type;
    Location _location;
    private String _name;
    private String _address;
    private int _classification;
}
