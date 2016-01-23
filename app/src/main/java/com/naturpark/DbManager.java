package com.naturpark;

/**
 * Created by frenzel on 12/20/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.naturpark.data.Obstacle;
import com.naturpark.data.Poi;
import com.naturpark.data.PoiType;
import com.naturpark.data.Route;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbManager extends SQLiteOpenHelper {
    private static String DB_PATH;
    private static String DB_PATH_PREFIX = "/data/data/";
    private static String DB_PATH_SUFFIX = "/databases/";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_KLASSIFIKATION = "classification";
    public static final String KEY_INFO = "info";

    private static final String TABLE_NAME = "poi";
 
    private SQLiteDatabase _database;

    public DbManager(Context context, boolean enforce_copy) {
        super(context, context.getString(R.string.database), null, 1);
        _init(context, enforce_copy);
    }

    public DbManager(Context context) {
        super(context, context.getString(R.string.database), null, 1);
        _init(context, false);
    }

    public void finalize()
    {
        close();
    }


    public void open() throws SQLException {
        _database = SQLiteDatabase.openDatabase(DB_PATH + getDatabaseName(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    public synchronized void close() {
        if (_database != null) {
            _database.close();
        }
        super.close();
    }

    public List<Route> queryRouteList() {

        List<Route> list_route = new ArrayList<Route>();

        try {
            Cursor cursor = _database.rawQuery("SELECT id, region, name, length, grade_avg, grade_max, quality, rating FROM Route;", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String region = cursor.getString(cursor.getColumnIndex("region"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                float length = cursor.getFloat(cursor.getColumnIndex("length"));
                int grade_avg = cursor.getInt(cursor.getColumnIndex("grade_avg"));
                int grade_max = cursor.getInt(cursor.getColumnIndex("grade_max"));
                int quality = cursor.getInt(cursor.getColumnIndex("quality"));
                int rating = cursor.getInt(cursor.getColumnIndex("rating"));

                list_route.add(new Route(id, region, name, length, grade_avg, grade_max, quality, rating));
                cursor.moveToNext();
            }
        }
        catch (SQLiteException e)
        {
            System.out.println("SQLiteException:" + e.getMessage());
        }

        return list_route;
    }

    public List<Obstacle> queryObstacleList() {

        List<Obstacle> list_obstacle = new ArrayList<Obstacle>();

        try {
            Cursor cursor = _database.rawQuery("SELECT _id,type, route_id, latitude, longitude, name FROM obstacle;", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                int route_id = cursor.getInt(cursor.getColumnIndex("route_id"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                String name = cursor.getString(cursor.getColumnIndex("name"));

                Location location = new Location("database");
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                list_obstacle.add(new Obstacle(id,type, route_id, location, name));

                cursor.moveToNext();
            }
        }
        catch (SQLiteException e)
        {
            System.out.println("SQLiteException:" + e.getMessage());
        }

        return list_obstacle;
    }

    public List<PoiType> queryPoiTypeList()
    {
        List<PoiType> list_poi_type = new ArrayList<PoiType>();

        try {
            Cursor cursor = _database.rawQuery("SELECT id, name, icon_name, visible FROM Poi_type;", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String iconName = cursor.getString(cursor.getColumnIndex("icon_name"));
                boolean visible = cursor.getInt(cursor.getColumnIndex("visible")) > 0;

                list_poi_type.add(new PoiType(id, name, iconName, visible));

                cursor.moveToNext();
            }
        }
        catch (SQLiteException e)
        {
            System.out.println("SQLiteException:" + e.getMessage());
        }

        return list_poi_type;
    }

    public List<Poi> queryPoiList()
    {
        List<Poi> list_poi = new ArrayList<Poi>();

        try {
            Cursor cursor = _database.rawQuery("SELECT type, latitude, longitude, name, address, classification,info FROM Poi;", null);
            cursor.moveToFirst();

            int id = 1;
            while (!cursor.isAfterLast()) {
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String classification = cursor.getString(cursor.getColumnIndex("classification"));
                String info = cursor.getString(cursor.getColumnIndex("info"));

                Location location = new Location("database");
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                list_poi.add(new Poi(id++, type, location, name, address, classification,info));

                cursor.moveToNext();
            }
        }
        catch (SQLiteException e)
        {
            System.out.println("SQLiteException:" + e.getMessage());
        }

        return list_poi;
    }

    public void update(PoiType poiType)
    {
        System.out.println("...............................................update:" + poiType.id());
        ContentValues values = new ContentValues();
        values.put("visible", poiType.is_visible());
        values.put("icon_name", poiType.iconName());
         _database.update("Poi_type", values, "id=" + poiType.id(), null);
    }

    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS obstacle";
        database.execSQL(query);
        onCreate(database);
    }
    private void copy(Context context, boolean enforce_copy) throws IOException {
        InputStream assetsDB = context.getAssets().open(getDatabaseName());
        File file = new File(DB_PATH);

        if (file.exists() == false) {
            file.mkdir();
        }

        File file_out = new File(DB_PATH + getDatabaseName());
        if (file_out.exists() && enforce_copy) {
            System.out.println("delete database file");
            file_out.delete();
        }

        OutputStream out = new FileOutputStream(DB_PATH + getDatabaseName());
        byte[] buffer = new byte[1024];
        int length;
        while ((length = assetsDB.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        out.flush();
        out.close();
    }

    private void _init(Context context, boolean enforce_copy) {

        DB_PATH = DB_PATH_PREFIX + context.getPackageName() + DB_PATH_SUFFIX + "/";
        try {
            copy(context, enforce_copy);
        }
        catch (IOException e)
        {
            System.out.println("IoException:" + e.getMessage());
        }

        try {
            open();
        }
        catch (SQLException e)
        {
            System.out.println("SQLException:" + e.getMessage());
        }
    }

    public Cursor fetchPoiByName(String inputText) throws SQLException {

        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = _database.query(TABLE_NAME, new String[]{KEY_ROWID, "type",
                            "name", "classification","latitude","longitude","info"},
                    null, null, null, null, null, null);

        } else {
            mCursor = _database.query(true, TABLE_NAME, new String[]{KEY_ROWID, "type",
                            "name","latitude","longitude", "classification","info"},
                    "name" + " like '%" + inputText + "%'", null,
                    null, null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllPoi() {

        Cursor mCursor = _database.query(TABLE_NAME, new String[]{KEY_ROWID, "type",
                        "name", "latitude", "longitude", "latitude", "longitude", "classification","info"},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public void insertObstacle(int type,float latitude,float longitude, String name){

        _database = this.getWritableDatabase();
        String query = "INSERT INTO obstacle (type,latitude,longitude,name) VALUES('"+type+"','"+latitude+"','"+longitude+"','"+name+"');";
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Insert: "+query);
        _database.execSQL(query);
    }
}