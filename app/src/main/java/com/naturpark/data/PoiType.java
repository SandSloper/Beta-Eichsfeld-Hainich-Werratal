package com.naturpark.data;

/**
 * Created by frenzel on 12/29/15.
 */
public class PoiType {

    public PoiType(int id, String name, String iconName) {
        _id = id;
        _name = name;
        _iconName = iconName;
    }

    public int id() { return _id; }
    public String name() { return _name; }
    public String iconName() { return _iconName; }

    private int _id;
    private String _name;
    private String _iconName;
}
