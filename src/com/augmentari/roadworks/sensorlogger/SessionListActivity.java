package com.augmentari.roadworks.sensorlogger;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.augmentari.roadworks.sensorlogger.dao.RecordingSessionDAO;
import com.augmentari.roadworks.sensorlogger.dao.SQLiteHelperImpl;
import com.augmentari.roadworks.sensorlogger.util.Formats;

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
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

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
                StringBuilder bldr = new StringBuilder(
                        DateFormat.format("MMM dd, hh:mm",
                                cursor.getLong(cursor.getColumnIndex(SQLiteHelperImpl.FIELD_START_TIME))))
                        .append("-")
                        .append(cursor.getLong(cursor.getColumnIndex(SQLiteHelperImpl.FIELD_EVENTS_LOGGED_COUNT)))
                        .append("-")
                        .append(cursor.getString(cursor.getColumnIndex(SQLiteHelperImpl.FIELD_STATE)));

                ((TextView) view).setText(bldr.toString());
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

    private RecordingSessionDAO recordingSessionDAO;

    public SessionListLoader(Context context) {
        super(context);
        recordingSessionDAO = new RecordingSessionDAO(context);
    }

    @Override
    public Cursor loadInBackground() {
        recordingSessionDAO.open();
        Cursor cursor = recordingSessionDAO.getAllSessionsSortById();

        return cursor;
    }
}
