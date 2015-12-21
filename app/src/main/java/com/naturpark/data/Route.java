package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Route implements Parcelable {

    public Route(int id, String name)
    {
        _id = id;
        _name = name;
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(_id);
        out.writeString(_name);
    }

    public static final Parcelable.Creator<Route> CREATOR
            = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    private Route(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
    }


    public int id() { return _id; }
    public String name() { return _name; }
    public int length() { return _length; }
    public int classification() { return _classification; }

    private int _id;
    private String _name;
    private int _length;
    private int _classification;
}
