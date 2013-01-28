package com.augmentari.roadworks.sensorlogger.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import com.augmentari.roadworks.model.RecordingSession;
import com.augmentari.roadworks.sensorlogger.util.Formats;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void openRead() {
        database = dbHelper.getReadableDatabase();
    }


    public void close() {
        dbHelper.close();
    }

    /**
     * Create new recording.
     * As a part of designed behavior, changes all sessions which have 'logging' state to 'FAILED'
     *
     * @param recordingSession
     * @return
     */
    public RecordingSession startNewRecordingSession(RecordingSession recordingSession) {
        if (Formats.isEmpty(recordingSession.getDataFileFullPath())) {
            throw new IllegalStateException("Should have start time set up prior to saving.");
        }
        recordingSession.setState(RecordingSession.State.LOGGING);

        ContentValues cvUpdateLoggingSessionsToFailed = new ContentValues();
        cvUpdateLoggingSessionsToFailed.put(SQLiteHelperImpl.FIELD_STATE, RecordingSession.State.FAILED.name());
        int rowsAffected = database.update(
                SQLiteHelperImpl.TABLE_SESSIONS,
                cvUpdateLoggingSessionsToFailed,
                SQLiteHelperImpl.FIELD_STATE + "=?",
                new String[]{RecordingSession.State.LOGGING.name()}
        );

        ContentValues cvCreateNewSession = new ContentValues();
        cvCreateNewSession.put(SQLiteHelperImpl.FIELD_DATA_FILE_PATH, recordingSession.getDataFileFullPath());
        cvCreateNewSession.put(SQLiteHelperImpl.FIELD_START_TIME, recordingSession.getStartTime().getTime());
        cvCreateNewSession.put(SQLiteHelperImpl.FIELD_STATE, RecordingSession.State.LOGGING.name());
        long insertId = database.insert(
                SQLiteHelperImpl.TABLE_SESSIONS,
                null,
                cvCreateNewSession);


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

    public Cursor getAllSessionsSortById() {
        return database.query(
                SQLiteHelperImpl.TABLE_SESSIONS,
                SQLiteHelperImpl.ALL_COLUMNS,
                "",
                null,
                null,
                null,
                SQLiteHelperImpl.FIELD_ID + " DESC");
    }

    public List<RecordingSession> getRecordingSessionsToUpload() {
        Cursor cursor = database.query(
                SQLiteHelperImpl.TABLE_SESSIONS,
                SQLiteHelperImpl.ALL_COLUMNS,
                SQLiteHelperImpl.FIELD_STATE + " = '" + RecordingSession.State.LOGGED.name() + "'",
                null, null, null, null);
        List<RecordingSession> recordingSessions = new ArrayList<RecordingSession>();
        while (cursor.moveToNext()) {
            recordingSessions.add(cursorToSession(cursor));
        }
        cursor.close();

        return recordingSessions;
    }

    public void markUploaded(long[] ids) {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteHelperImpl.FIELD_STATE, RecordingSession.State.UPLOADED.name());
        if (ids == null || ids.length == 0) {
            return; // no data to update.
        }

        StringBuilder idsString = new StringBuilder(Long.toString(ids[0])); // no issue - see check above.
        for (int i = 1; i < ids.length; i++) {
            //starting from the second
            idsString.append(",").append(ids[i]);
        }

        database.update(
                SQLiteHelperImpl.TABLE_SESSIONS,
                cv,
                SQLiteHelperImpl.FIELD_ID + " in (" + idsString.toString() + ") ",
                null);

    }

    private RecordingSession cursorToSession(Cursor cursor) {
        RecordingSession sess = new RecordingSession();

        sess.setId(cursor.getLong(0));
        sess.setStartTime(new Date(cursor.getLong(1)));
        sess.setEndTime(new Date(cursor.getLong(2)));
        sess.setState(RecordingSession.State.valueOf(cursor.getString(3)));
        sess.setDataFileFullPath(cursor.getString(4));
        sess.setEventsLogged(cursor.getLong(5));

        return sess;
    }

    public static JSONObject sessionToJSONObject(RecordingSession session) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", session.getId());
        object.put("startTime", Formats.formatJsonDate(session.getStartTime()));
        object.put("endTime", Formats.formatJsonDate(session.getEndTime()));
        object.put("eventsLogged", session.getEventsLogged());

        return object;
    }
}
