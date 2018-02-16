package com.example.dilki.mynotes;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dilki.mynotes.data.DataContract;

/**
 * Created by dilki on 16/02/2018.
 */

public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private long item_id;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            item_id = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);

        // Show the dummy content as text in a TextView.
        if (item_id > 0) {
            Uri contentUri = ContentUris.withAppendedId(DataContract.DataEntry.CONTENT_URI, item_id);

            Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
            if (cursor != null) {
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

                    ((TextView) rootView.findViewById(R.id.title_text_frag)).setText(title);
                    ((TextView) rootView.findViewById(R.id.note_text_frag)).setText(note);
                    ((TextView) rootView.findViewById(R.id.date_text_frag)).setText(date);
                }
                cursor.close();
            }
        }

        return rootView;
    }
}
