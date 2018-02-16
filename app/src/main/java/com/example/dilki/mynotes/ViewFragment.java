package com.example.dilki.mynotes;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dilki.mynotes.data.DataContract;

public class ViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_NOTE_LOADER = 1;
    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentNoteUri;
    private TextView mNoteText;
    private TextView mTitleText;
    private TextView mDateText;

    public ViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mCurrentNoteUri != null) {
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_note, container, false);

        int id = rootView.getId();
        mCurrentNoteUri = ContentUris.withAppendedId(DataContract.DataEntry.CONTENT_URI, id);

        mNoteText = rootView.findViewById(R.id.note_text_frag);
        mTitleText = rootView.findViewById(R.id.title_text_frag);
        mDateText = rootView.findViewById(R.id.date_text_frag);

        if (mCurrentNoteUri != null) {
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
        }

        mNoteText = rootView.findViewById(R.id.note_text_frag);
        mTitleText = rootView.findViewById(R.id.title_text_frag);
        mDateText = rootView.findViewById(R.id.date_text_frag);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DataContract.DataEntry._ID,
                DataContract.DataEntry.COLUMN_TITLE,
                DataContract.DataEntry.COLUMN_NOTE,
                DataContract.DataEntry.COLUMN_DATE,
                DataContract.DataEntry.COLUMN_BACKUP};

        // This loader will execute the ContentProvider's query method on a background thread
        return new android.content.CursorLoader(getActivity(),   // Parent activity context
                mCurrentNoteUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_TITLE);
            int noteColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NOTE);
            int backupColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_BACKUP);
            int dateColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_DATE);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String note = cursor.getString(noteColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            int backup = cursor.getInt(backupColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleText.setText(title);
            mNoteText.setText(note);
            mDateText.setText(date);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
