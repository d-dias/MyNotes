package com.example.dilki.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dilki.mynotes.data.DataContract;

/**
 * Created by dilki on 16/02/2018.
 */

public class ItemPagerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter adapter;

    public ItemPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.item_detail_activity, container, false);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = rootView.findViewById(R.id.pager);
        adapter = new PagerAdapter(getFragmentManager(), null);
        mPager.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
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
        return new CursorLoader(getContext(),   // Parent activity context
                DataContract.DataEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}

class PagerAdapter extends FragmentStatePagerAdapter {

    private Cursor mCursor;

    public PagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        mCursor = cursor;
    }

    @Override
    public Fragment getItem(int position) {
        if (mCursor.moveToPosition(position)) {
            Bundle arguments = new Bundle();
            arguments.putLong(ItemDetailFragment.ARG_ITEM_ID, mCursor.getLong(mCursor.getColumnIndex(DataContract.DataEntry._ID)));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}

