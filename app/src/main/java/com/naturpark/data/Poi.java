package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.location.Location;

public class Poi {

    // generate a runtime dynamic id, used during for easy identification in app
    static private int Id = 1;

    private int _id;
    private int _type;
    Location _location;
    private String _name;
    private String _address;
    private String _classification;
    private String _info;

    public Poi(int type, Location location, String name, String address,String classification,String info)
    {
        _id = Id++;
        _type = type;
        _location = location;
        _name = name;
        _address = address;
        _classification = classification;
        _info = info;
    }

    public int id() { return _id; }
    public int type() { return _type; }
    public Location location() { return _location; }
    public String name() { return _name; }
    public String address() { return _address; }
    public String classification() { return _classification; };
    public String info() { return _info; };

    public int classification_id() {
        if (_classification == "Geeignet")
            return 3;
        if (_classification == "Bedingt")
            return 2;
        if (_classification == "Ungeeignet")
            return 1;

        return 0;
    }
}
