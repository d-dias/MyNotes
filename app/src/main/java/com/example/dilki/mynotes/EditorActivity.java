package com.example.dilki.mynotes;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dilki.mynotes.data.DataContract;

import java.util.Objects;

/**
 * Created by dilki on 24/01/2018.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "EditorActivity";

    /**
     * EditText field to enter the title
     */
    private EditText mTitleEditText;

    /**
     * EditText field to enter the note
     */
    private LinedEditText mNoteEditText;

    /**
     * EditText field to enter the backup
     */
    private Spinner mBackUpSpinner;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentNoteUri;

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_NOTE_LOADER = 1;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mBackUp = DataContract.DataEntry.BACKUP_NO;

    private boolean mNoteHasChanged = false;

    private String titleString;
    private String noteString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mTitleEditText = findViewById(R.id.edit_title);
        mNoteEditText = findViewById(R.id.edit_note);
        mBackUpSpinner = findViewById(R.id.backup_spinner);
        LinearLayout editLayout = findViewById(R.id.edit_layout);

        if (mCurrentNoteUri == null) {
            setTitle(getString(R.string.add_new_note));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_note));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);

            editLayout.setFocusableInTouchMode(true);
            mTitleEditText.clearFocus();
            mNoteEditText.clearFocus();
            editLayout.requestFocus();
        }

        setupSpinner();

        mTitleEditText.setOnTouchListener(mTouchListener);
        mNoteEditText.setOnTouchListener(mTouchListener);
        mBackUpSpinner.setOnTouchListener(mTouchListener);

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_backup_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mBackUpSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mBackUpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.backup_no))) {
                        mBackUp = DataContract.DataEntry.BACKUP_NO;
                    } else {
                        mBackUp = DataContract.DataEntry.BACKUP_YES;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBackUp = 0; // Unknown
            }
        });
    }

    private void saveNote() {

        titleString = mTitleEditText.getText().toString().trim();
        noteString = mNoteEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(DataContract.DataEntry.COLUMN_TITLE, titleString);
        values.put(DataContract.DataEntry.COLUMN_NOTE, noteString);
        values.put(DataContract.DataEntry.COLUMN_BACKUP, mBackUp);

        if (TextUtils.isEmpty(titleString) && TextUtils.isEmpty(noteString)) {
            return;
        }

        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentNoteUri == null) {

            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(DataContract.DataEntry.CONTENT_URI, values);
            if (mBackUp == 1) {
                sendMail("New note");
            }
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {

                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.failed_to_insert_new_note,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.successfully_insert_new_note,
                        Toast.LENGTH_SHORT).show();
            }

        } else {

            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);

            if (mBackUp == 1) {
                sendMail("Update note");

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {

                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.failed_to_update_note,
                        Toast.LENGTH_SHORT).show();
                }
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.successfully_updated_note,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMail(String noteType) {
        String TO = "dilkiedias123@gmail.com";
        String mailSubject;
        String mailMassage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(noteType, "New note")) {
                mailSubject = "New note: " + titleString;
                mailMassage = "You added a new note today.\n" + noteString;
            } else {
                mailSubject = "Update note: " + titleString;
                mailMassage = "You updated a note today.\n" + noteString;
            }
        }else {
            if (noteType == "New note") {
                mailSubject = "New note: " + titleString;
                mailMassage = "You added a new note today.\n" + noteString;
            } else {
                mailSubject = "Update note: " + titleString;
                mailMassage = "You updated a note today.\n" + noteString;
            }
        }

        try {
            GMailSender sender = new GMailSender("mynotesbackupmail@gmail.com", "netballSCG123");
            sender.sendMail(mailSubject,
                    mailMassage,
                    TO,
                    "user@yahoo.com");
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }

    }


    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_note_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Delete" button, so delete the pet.
                deleteNote();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteNote() {

        // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
        // and pass in the new ContentValues. Pass in null for the selection and selection args
        // because mCurrentPetUri will already identify the correct row in the database that
        // we want to modify.
        int rowsAffected = getContentResolver().delete(mCurrentNoteUri, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {

            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save pet to database
                saveNote();
                // exit editor
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show a dialog that notifies the user going to delete the pet
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNoteHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DataContract.DataEntry._ID,
                DataContract.DataEntry.COLUMN_TITLE,
                DataContract.DataEntry.COLUMN_NOTE,
                DataContract.DataEntry.COLUMN_BACKUP};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
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

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String note = cursor.getString(noteColumnIndex);
            int backup = cursor.getInt(backupColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mNoteEditText.setText(note);

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (backup) {
                case DataContract.DataEntry.BACKUP_NO:
                    mBackUpSpinner.setSelection(1);
                    break;
                case DataContract.DataEntry.BACKUP_YES:
                    mBackUpSpinner.setSelection(2);
                    break;
                default:
                    mBackUpSpinner.setSelection(1);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentNoteUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Defines a custom EditText View that draws lines between each line of text that is displayed.
     */
    public static class LinedEditText extends android.support.v7.widget.AppCompatEditText {

        private Rect mRect;
        private Paint mPaint;

        // This constructor is used by LayoutInflater
        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            // Creates a Rect and a Paint object, and sets the style and color of the Paint object.
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.BLUE);
        }

        /**
         * This is called to draw the LinedEditText object
         *
         * @param canvas The canvas on which the background is drawn.
         */
        @Override
        protected void onDraw(Canvas canvas) {

            int height = getHeight();
            int line_height = getLineHeight();

            // Gets the number of lines of text in the View.
            int count = height / line_height;

            if (getLineCount() > count)
                count = getLineCount();

            // Gets the global Rect and Paint objects
            Rect r = mRect;
            Paint paint = mPaint;
            int baseline = getLineBounds(0, r);

            /*
             * Draws one line in the rectangle for every line of text in the EditText
             */
            for (int i = 0; i < count; i++) {

                // Gets the baseline coordinates for the current line of text
                /*
                 * Draws a line in the background from the left of the rectangle to the right,
                 * at a vertical position one dip below the baseline, using the "paint" object
                 * for details.
                 */
                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
                baseline += getLineHeight();

                // Finishes up by calling the parent method
                super.onDraw(canvas);

            }
        }
    }
}
