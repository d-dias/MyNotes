package com.example.dilki.mynotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.dilki.mynotes.data.DataContract;

/**
 * Created by dilki on 24/01/2018.
 */

public class NotesCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link NotesCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NotesCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);

    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO: Fill out this method

        TextView titleText = view.findViewById(R.id.title_text);
        TextView noteText = view.findViewById(R.id.note_text);
        TextView dateText = view.findViewById(R.id.date_text);

        int titleColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_TITLE);
        int noteColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_NOTE);
        int dateColumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_DATE);
        int idColumnIndex = cursor.getColumnIndex(DataContract.DataEntry._ID);

        int currentId = cursor.getInt(idColumnIndex);
        Log.e("id", String.valueOf(currentId));
        String currentTitle = cursor.getString(titleColumnIndex);
        String currentNote = cursor.getString(noteColumnIndex);
        String currentDate = cursor.getString(dateColumnIndex);
        //String currentGender = cursor.getString(genderColumnIndex);
        //int currentWeight = cursor.getInt(weightColumnIndex);

        titleText.setText(currentTitle);
        noteText.setText(currentNote);
        dateText.setText(currentDate);
    }
}
