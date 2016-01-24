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
import android.util.Log;

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
import java.util.List;

public class DbManager extends SQLiteOpenHelper {

    private final Context myContext;
    private static final String DATABASE_NAME = "naturpark.db";
    public static String DATABASE_PATH = "";
    public static final int DATABASE_VERSION = 1;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_KLASSIFIKATION = "classification";
    public static final String KEY_INFO = "info";

    private static final String TABLE_NAME = "poi";

    private SQLiteDatabase _database;

    public DbManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DATABASE_PATH = myContext.getDatabasePath(DATABASE_NAME).toString();
    }

    //Create a empty database on the system
    public void create() throws IOException
    {
        boolean dbExist = checkDataBase();
        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }
        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e)
        {
        }
        return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder

    private void copyDataBase() throws IOException
    {
        String outFileName = DATABASE_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    //delete database
    public void db_delete()
    {
        File file = new File(DATABASE_PATH);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    //Open database
    public void open() throws SQLException
    {
        String myPath = DATABASE_PATH;
        _database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(_database != null)
            _database.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db)
    {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }
    //add your public methods for insert, get, delete and update data in database.

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

                list_poi.add(new Poi(type, location, name, address, classification,info));

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