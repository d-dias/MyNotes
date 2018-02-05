package com.example.dilki.mynotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dilki on 24/01/2018.
 */

public class NoteDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mynotes.db";
    public static final int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
// Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_NOTES_TABLE =  "CREATE TABLE " + DataContract.DataEntry.NOTES_TABLE_NAME + " ("
                + DataContract.DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataContract.DataEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + DataContract.DataEntry.COLUMN_NOTE + " TEXT NOT NULL, "
                + DataContract.DataEntry.COLUMN_BACKUP + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
