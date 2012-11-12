package com.augmentari.roadworks.sensorlogger.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.augmentari.roadworks.sensorlogger.model.RecordingSession;
import com.augmentari.roadworks.sensorlogger.util.Formats;

import java.util.Date;

/**
 * DAO class to manipulate the Recording sessions. We store all the meta-data in the database, and only raw data file
 * on the filesystem. The file contains all the events and is used at the server side for the future processing.
 */
public class RecordingSessionDAO {

    private SQLiteDatabase database;

    private SQLiteHelperImpl dbHelper;

    public RecordingSessionDAO(Context context) {
        dbHelper = new SQLiteHelperImpl(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Create new recording.
     *
     * @param recordingSession
     * @return
     */
    public RecordingSession createRecordingSession(RecordingSession recordingSession) {
        if (Formats.isEmpty(recordingSession.getDataFileFullPath())) {
            throw new IllegalStateException("Should have start time set up prior to saving.");
        }
        recordingSession.setState(RecordingSession.State.LOGGING);

        ContentValues cv = new ContentValues();
        cv.put(SQLiteHelperImpl.FIELD_DATA_FILE_PATH, recordingSession.getDataFileFullPath());
        cv.put(SQLiteHelperImpl.FIELD_START_TIME, recordingSession.getStartTime().getTime());
        cv.put(SQLiteHelperImpl.FIELD_STATE, RecordingSession.State.LOGGING.name());
        long insertId = database.insert(
                SQLiteHelperImpl.TABLE_SESSIONS,
                null,
                cv);

        return getSessionById(insertId);
    }

    public void finishSession(long sessionId, long statementsLogged, Date logEndTime) {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteHelperImpl.FIELD_END_TIME, logEndTime.getTime());
        cv.put(SQLiteHelperImpl.FIELD_EVENTS_LOGGED_COUNT, statementsLogged);
        cv.put(SQLiteHelperImpl.FIELD_STATE, RecordingSession.State.LOGGED.name());

        database.update(
                SQLiteHelperImpl.TABLE_SESSIONS,
                cv,
                SQLiteHelperImpl.FIELD_ID + " = " + sessionId,
                null);
    }

    public RecordingSession getSessionById(long id) {
        Cursor cursor = database.query(
                SQLiteHelperImpl.TABLE_SESSIONS,
                SQLiteHelperImpl.ALL_COLUMNS,
                SQLiteHelperImpl.FIELD_ID + " = " + id,
                null, null, null, null);
        cursor.moveToFirst();

        RecordingSession session = cursorToSession(cursor);
        cursor.close();

        return session;
    }

    private RecordingSession cursorToSession(Cursor cursor) {
        RecordingSession sess = new RecordingSession();
//        public static final String[] ALL_COLUMNS = {
//                FIELD_ID,
//                FIELD_START_TIME,
//                FIELD_END_TIME,
//                FIELD_STATE,
//                FIELD_DATA_FILE_PATH,
//                FIELD_EVENTS_LOGGED_COUNT
//        };

        sess.setId(cursor.getLong(0));
        sess.setStartTime(new Date(cursor.getLong(1)));
        sess.setEndTime(new Date(cursor.getLong(2)));
        sess.setState(RecordingSession.State.valueOf(cursor.getString(3)));
        sess.setDataFileFullPath(cursor.getString(4));
        sess.setEventsLogged(cursor.getLong(5));

        return sess;
    }
}
