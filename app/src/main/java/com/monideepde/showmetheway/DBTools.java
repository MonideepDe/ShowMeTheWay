package com.monideepde.showmetheway;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class DBTools extends SQLiteOpenHelper {

    private static final String TAG_DB_TOOLS = DBTools.class.getSimpleName();
    public static final String destination_tableName = "destinations";
    public static final String destination_name = "name";
    public static final String destination_GPSCoord="gpscoord";

    private static DBTools dbToolsInstance = null;

    public static DBTools getInstance (Context context){
        if(dbToolsInstance == null) {
            dbToolsInstance = new DBTools(context);
        }

        return dbToolsInstance;
    }

    protected DBTools(Context applicationContext) {
        super(applicationContext, "destination_locations.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG_DB_TOOLS, "Entered onCreate");
        String query = "CREATE TABLE " + destination_tableName + " ( " + destination_name + " TEXT PRIMARY KEY, " +
                destination_GPSCoord +" TEXT )";
        Log.d(TAG_DB_TOOLS, "Query to be executed: " + query);
        db.execSQL(query);
        Log.d(TAG_DB_TOOLS, "Destination table created!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG_DB_TOOLS, "Entered onUpgrade");
        String query = "DROP TABLE IF EXISTS " + destination_tableName;
        Log.d(TAG_DB_TOOLS, "Query to be executed: " + query);
        db.execSQL(query);
        onCreate(db);
        Log.d(TAG_DB_TOOLS, "Exiting onUpgrade");
    }

    public void insertDestination(HashMap<String, String> queryValues) {
        Log.d(TAG_DB_TOOLS, "Entered insertDestination");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String destName = queryValues.get(destination_name);
        String gpscoords = queryValues.get(destination_GPSCoord);
        Log.d(TAG_DB_TOOLS, "insertDestination: destination name = " + destName + " ; GPS Coord = " + gpscoords);

        values.put(destination_name, destName);
        values.put(destination_GPSCoord, gpscoords);

        db.insert(destination_tableName, null, values);

        db.close();
        Log.d(TAG_DB_TOOLS, "Exiting insertDestination");
    }

    public int updateDestination(HashMap<String, String> queryValues) {
        Log.d(TAG_DB_TOOLS, "Entered updateDestination");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("GPSCoords", queryValues.get(destination_GPSCoord));

        int result = db.update(destination_tableName, values, destination_name + "= ?", new String[] {queryValues.get(destination_name)});
        db.close();

        Log.d(TAG_DB_TOOLS, "Returning updateDestination");
        return result;
    }

    public void deleteDestination(String name) {
        Log.d(TAG_DB_TOOLS, "Entered deleteDestination");
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + destination_tableName + "where " + destination_name + " ='" +  name +"'";
        Log.d(TAG_DB_TOOLS, "Query to be executed: " + deleteQuery);
        db.execSQL(deleteQuery);
        Log.d(TAG_DB_TOOLS, "Exiting deleteDestination");
    }

    public ArrayList<String> getAllDestinationNames() {
        Log.d(TAG_DB_TOOLS, "Entered getAllDestinationNames");

        ArrayList<String> destNames = new ArrayList<>();

        String selectQuery = "SELECT name FROM destinations";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                destNames.add(cursor.getString(0));
                Log.d(TAG_DB_TOOLS, "getAllDestinationNames: name = " + cursor.getString(0));
            } while(cursor.moveToNext());
        } else {
            Log.d(TAG_DB_TOOLS, "getAllDestinationNames: Nothing in database");
        }
        Log.d(TAG_DB_TOOLS, "Returning getAllDestinationNames");
        return destNames;
    }

    public ArrayList<HashMap<String, String>> getAllDestinationDetails() {
        Log.d(TAG_DB_TOOLS, "Entering getAllDestinationDetails");

        ArrayList<HashMap<String, String>> allDestinationDetails = new ArrayList<>();
        String selectQuery =  "SELECT * FROM " + destination_tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do {
                HashMap<String, String> destInfo = new HashMap<>();

                destInfo.put(destination_name, cursor.getString(0));
                destInfo.put(destination_GPSCoord, cursor.getString(1));

                allDestinationDetails.add(destInfo);

            }while(cursor.moveToNext());
        }
        Log.d(TAG_DB_TOOLS, "Returning getAllDestinationDetails");
        return allDestinationDetails;
    }

    public String getGPSCoordFromName(String name) {
        Log.d(TAG_DB_TOOLS, "Entering getGPSCoordFromName");

        ArrayList<HashMap<String, String>> allDestinationDetails = new ArrayList<>();
        String selectQuery =  "SELECT " + destination_GPSCoord + " FROM " + destination_tableName + " WHERE " + destination_name + " = '" + name + "'";
        Log.d(TAG_DB_TOOLS, "Select Query Formed: " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        String GPSCoordinates="";

        if(cursor.moveToFirst()) {
            GPSCoordinates=cursor.getString(0);
        }
        db.close();

        Log.d(TAG_DB_TOOLS, "Exiting getGPSCoordFromName; GPS Coordinates received = " + GPSCoordinates);
        return GPSCoordinates;
    }

}
