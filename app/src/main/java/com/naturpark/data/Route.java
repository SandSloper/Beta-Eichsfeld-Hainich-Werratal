package com.naturpark.data;

/**
 * Created by frenzel on 12/20/15.
 */
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

public class Route implements Parcelable {

    public Route(int id, String region, String name, float length, int grade_avg, int grade_max, int quality, int rating)
    {
        _id = id;
        _region = region;
        _name = name;
        _length = length;
        _grade_avg = grade_avg;
        _grade_max = grade_max;
        _quality = quality;
        _rating =  rating;
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

    public BoundingBoxE6 boundingBox(Context context) {
        return BoundingBoxE6.fromGeoPoints(getTrack(context));
    }


    public int id() { return _id; }
    public String region() { return _region; }
    public String name() { return _name; }
    public float length() { return _length; }
    public int slope_avg() { return _grade_avg; }
    public int slope_max() { return _grade_max; }
    public int quality() { return _quality; }
    public int rating() { return _rating; }

    public ArrayList<GeoPoint> getTrack(Context context) {
        ArrayList<GeoPoint> list = new ArrayList<>();

        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(context.getAssets().open("tracks/" + id() + ".gpx"), null);

            int parserEvent = parser.getEventType();
            while (parserEvent != XmlPullParser.END_DOCUMENT) {

                switch (parserEvent) {

                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();

                        if (tag.compareTo("trkpt") == 0) {
                            double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                            list.add(new GeoPoint(lat, lon));
                        } else if (tag.compareTo("wpt") == 0) {
                            double lat = Double.parseDouble(parser.getAttributeValue(null, "lat"));
                            double lon = Double.parseDouble(parser.getAttributeValue(null, "lon"));
                            String description = parser.getAttributeValue(null, "description");
                            //Log.i("", "   waypoint=" + " latitude=" + lat + " longitude=" + lon + " " + description);
                        }
                        break;
                }

                parserEvent = parser.next();
            }

        } catch (Exception e) {
            Log.i("RouteLoader", "Failed in parsing XML", e);
        }

        return list;
    }

    private int _id;
    private String _region;
    private String _name;
    private float _length;
    private int _grade_avg;
    private int _grade_max;
    private int _quality;
    private int _rating;
}
