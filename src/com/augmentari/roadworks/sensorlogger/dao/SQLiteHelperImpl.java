package com.augmentari.roadworks.sensorlogger.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.augmentari.roadworks.sensorlogger.util.Log;

/**
 * Database helper implementation - will use for accessing DB.
 */
public class SQLiteHelperImpl extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DB_FILENAME = "SensorLogger.db";

    public static final String TABLE_SESSIONS = "session";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_START_TIME = "start_time";
    public static final String FIELD_END_TIME = "end_time";
    public static final String FIELD_DATA_FILE_PATH = "data_file_path";
    public static final String FIELD_EVENTS_LOGGED_COUNT = "events_logged";

    public static final String DB_CREATE_CODE = "CREATE TABLE " + TABLE_SESSIONS + " ( "
            + FIELD_ID + " integer primary key autoincrement, "
            + FIELD_START_TIME + " integer, "
            + FIELD_END_TIME + " integer, "
            + FIELD_STATE + " text, "
            + FIELD_DATA_FILE_PATH + " text, "
            + FIELD_EVENTS_LOGGED_COUNT + " integer)";

    public static final String[] ALL_COLUMNS = {
            FIELD_ID,
            FIELD_START_TIME,
            FIELD_END_TIME,
            FIELD_STATE,
            FIELD_DATA_FILE_PATH,
            FIELD_EVENTS_LOGGED_COUNT
    };

    public SQLiteHelperImpl(Context context) {
        super(context, DB_FILENAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE_CODE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }
}
