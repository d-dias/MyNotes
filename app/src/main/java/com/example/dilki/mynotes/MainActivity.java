package com.example.dilki.mynotes;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dilki.mynotes.data.DataContract;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int NOTE_LOADER = 0;
    public static int items;
    /** Adapter for the ListView */
    private NotesCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FrameLayout frame = findViewById(R.id.container);
        frame.setVisibility(View.GONE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        GridView noteListView = findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        noteListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new NotesCursorAdapter(this, null);
        noteListView.setAdapter(mCursorAdapter);

        // set up on item click
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (frame.getVisibility() == View.GONE) {

                    frame.setVisibility(View.VISIBLE);

                    ItemPagerFragment fragment = new ItemPagerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong("id", id);
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.commit();
                }

                // final ViewPager viewPager = findViewById(R.id.pager);
                // PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
                // viewPager.setAdapter(pagerAdapter);
                // viewPager.setOffscreenPageLimit(3);
                // viewPager.setPageMargin(100);
                // viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                //     @Override
                //     public void transformPage(View page, float position) {
                //         int pageWidth = viewPager.getMeasuredWidth() - viewPager.getPaddingLeft() - viewPager.getPaddingRight();
                //         int pageHeight = viewPager.getHeight();
                //         int paddingLeft = viewPager.getPaddingLeft();
                //         float transformPos = (float) (page.getLeft() - (viewPager.getScrollX() + paddingLeft)) / pageWidth;
                //         final float normalizedposition = Math.abs(Math.abs(transformPos) - 1);
                //         page.setAlpha(normalizedposition + 0.5f);
                //         int max = -pageHeight / 10;
//
                // /* Check http://stackoverflow.com/questions/32384789/android-viewpager-smooth-transition-for-this-design
                //    for other ways to do this.
                // */
                //         if (transformPos < -1) { // [-Infinity,-1)
                //             // This page is way off-screen to the left.
                //             page.setTranslationY(0);
                //         } else if (transformPos <= 1) { // [-1,1]
                //             page.setTranslationY(max * (1 - Math.abs(transformPos)));
                //         } else { // (1,+Infinity]
                //             // This page is way off-screen to the right.
                //             page.setTranslationY(0);
                //         }
                //     }
                // });

                // FragmentManager fm = getFragmentManager();
                // NoteFragment dialogFragment = new NoteFragment();

                // Supply num input as an argument.
                // Bundle args = new Bundle();
                // Uri currentPetUri = ContentUris.withAppendedId(DataContract.DataEntry.CONTENT_URI, id);
                // args.putString("currentUri", currentPetUri.toString());
                // dialogFragment.setArguments(args);

                // dialogFragment.show(fm, "Sample Fragment");

                // Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                // Uri currentPetUri = ContentUris.withAppendedId(DataContract.DataEntry.CONTENT_URI, id);
                // intent.setData(currentPetUri);
                // startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(NOTE_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {

        int rowsAffected = getContentResolver().delete(DataContract.DataEntry.CONTENT_URI, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {

            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.failed_to_delete_all_notes),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.successfully_deleted_all_notes),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Todo: Use this methode to delete all
    private void showDeleteConfirmationDialog() {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_notes_message);
        builder.setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                // User clicked the "Delete" button, so delete the pet.
                deleteAllPets();
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DataContract.DataEntry._ID,
                DataContract.DataEntry.COLUMN_TITLE,
                DataContract.DataEntry.COLUMN_NOTE,
                DataContract.DataEntry.COLUMN_DATE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                DataContract.DataEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        items = data.getCount();
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
