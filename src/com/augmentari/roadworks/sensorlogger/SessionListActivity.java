package com.augmentari.roadworks.sensorlogger;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.augmentari.roadworks.sensorlogger.dao.RecordingSessionDAO;
import com.augmentari.roadworks.sensorlogger.dao.SQLiteHelperImpl;
import com.augmentari.roadworks.sensorlogger.model.RecordingSession;

/**
 * Activity showing a list of the sessions with their info (status, kilometers logged etc).
 */
public class SessionListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {

    // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setOnItemLongClickListener(this);

        // Create a progress bar to display while the list loads
        TextView textView = new TextView(this);
        textView.setText("No data. Please record some sessions first");
        textView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER));
        getListView().setEmptyView(textView);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(textView);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {SQLiteHelperImpl.FIELD_ID};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                String stateString = cursor.getString(cursor.getColumnIndex(SQLiteHelperImpl.FIELD_STATE));
                RecordingSession.State state = RecordingSession.State.valueOf(stateString);

                StringBuilder bldr = new StringBuilder(
                        DateFormat.format("MMM dd, hh:mm",
                                cursor.getLong(cursor.getColumnIndex(SQLiteHelperImpl.FIELD_START_TIME))))
                        .append("-")
                        .append(cursor.getLong(cursor.getColumnIndex(SQLiteHelperImpl.FIELD_EVENTS_LOGGED_COUNT)))
                        .append("-")
                        .append(stateString);

                ((TextView) view).setText(bldr.toString());

                int color = Color.GRAY;
                switch (state) {
                    case LOGGING:
                        color = Color.WHITE;
                        break;

                    case LOGGED:
                        color = Color.YELLOW;
                        break;

                    case PROCESSED:
                    case PROCESSING:
                        color = Color.BLUE;
                        break;

                    case UPLOADED:
                    case UPLOADING:
                        color = Color.GREEN;
                        break;
                }
                ((TextView) view).setTextColor(color);
                return true;
            }
        });
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SessionListLoader(this);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(this, "on List item Clicked", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, "on List item LOOONG-Clicked", Toast.LENGTH_SHORT);
        return false;
    }
}

class SessionListLoader extends SimpleCursorLoader {

    //TODO: how do I check if there is really a mem leak?
    // http://stackoverflow.com/questions/5796611/dialog-throwing-unable-to-add-window-token-null-is-not-for-an-application-wi
    private ProgressDialog dialog = null;

    private RecordingSessionDAO recordingSessionDAO;

    private final Context activityContext;

    public SessionListLoader(Context context) {
        super(context);
        this.activityContext = context;
        recordingSessionDAO = new RecordingSessionDAO(context);
    }

    @Override
    public Cursor loadInBackground() {
        recordingSessionDAO.open();
        Cursor cursor = recordingSessionDAO.getAllSessionsSortById();
        return cursor;
    }

    @Override
    public void onCanceled(Cursor cursor) {
        dialog.dismiss();
        super.onCanceled(cursor);
    }

    @Override
    public void deliverResult(Cursor cursor) {
        dialog.dismiss();
        super.deliverResult(cursor);
    }

    @Override
    protected void onStartLoading() {
        dialog = ProgressDialog.show(
                activityContext,
                activityContext.getString(R.string.loading_title),
                activityContext.getString(R.string.loading_text),
                true,
                false);
        super.onStartLoading();
    }
}

