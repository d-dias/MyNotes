package com.example.dilki.mynotes.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dilki on 24/01/2018.
 */

public class DataContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DataContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.dilki.mynotes";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.data/data/ is a valid path for
     * looking at data data. content://com.example.android.data/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */

    public static final String PATH_NOTES = "notes";

    public static class DataEntry implements BaseColumns {

        public static final String NOTES_TABLE_NAME = "notes";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_BACKUP = "backup";

        public static final int BACKUP_YES = 1;
        public static final int BACKUP_NO = 0;

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

    }
}

