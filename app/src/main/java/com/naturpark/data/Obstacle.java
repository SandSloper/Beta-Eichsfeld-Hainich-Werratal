package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.location.Location;

public class Obstacle {
    static final int Engstelle = 1;
    static final int Bla = 2;
    private String _name;
    private int _id;

    public Obstacle(int id,int type, int route_id, Location location, String name)
    {
        _id = id;
        _type = type;
        _route_id = route_id;
        _location = location;
        _name = name;
    }
    public int id() { return  _id; }
    public int type() { return  _type; }
    public String name() { return _name; }
    public int route_id() { return _route_id; }
    public Location location() { return _location; }

    private int _type = 0;
    private int _route_id = 0;
    private Location _location;
}
