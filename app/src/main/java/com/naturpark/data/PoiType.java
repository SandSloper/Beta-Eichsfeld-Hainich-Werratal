package com.naturpark.data;

/**
 * Created by frenzel on 12/29/15.
 */
public class PoiType {

    public PoiType(int id, String name, String iconName) {
        _id = id;
        _name = name;
        _iconName = iconName;
        _visible = true;
    }

    public int id() { return _id; }
    public String name() { return _name; }
    public String icon_name() { return _iconName; }
    public boolean is_visible() { return _visible; }

    void hide() {
        _visible = false;
    }

    void show() {
        _visible = true;
    }

    private int _id;
    private String _name;
    private String _iconName;

    private boolean _visible;
}
