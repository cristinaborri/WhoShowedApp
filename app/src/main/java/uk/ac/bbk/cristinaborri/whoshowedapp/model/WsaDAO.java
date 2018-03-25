package uk.ac.bbk.cristinaborri.whoshowedapp.model;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Cristina Borri
 * This class will provide the methods that will allow to open and close the application database
 */
abstract class WsaDAO {
    private static final String LOGTAG = "WSA_DATABASE";
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;

    public void open(){
        Log.i(LOGTAG,"Database Opened");
        database = dbHandler.getWritableDatabase();
    }

    public void close(){
        Log.i(LOGTAG, "Database Closed");
        dbHandler.close();
    }
}
