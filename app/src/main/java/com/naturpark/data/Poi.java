package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.location.Location;

public class Poi {

    private int _type;
    Location _location;
    private String _name;
    private String _address;
    private String _classification;
    private String _info;

    public Poi(int type, Location location, String name, String address,String classification,String info)
    {
        _type = type;
        _location = location;
        _name = name;
        _address = address;
        _classification = classification;
        _info = info;
    }

    public int type() { return _type; }
    public Location location() { return _location; }
    public String name() { return _name; }
    public String address() { return _address; }
    public String classification() { return _classification; };
    public String info() { return _info; };
}