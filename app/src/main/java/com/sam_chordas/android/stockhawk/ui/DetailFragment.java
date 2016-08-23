package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mSymbol;
    private static final int CURSOR_LOADER_ID = 2;
    private static final String[] DETAIL_COLUMNS = new String[]{
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.NAME};

    public static final int COL_ID = 0;
    public static final int COL_SYMBOL = 1;
    public static final int COL_BIDPRICE = 2;
    public static final int COL_PERCENT_CHANGE = 3;
    public static final int COL_CHANGE = 4;
    public static final int COL_NAME = 5;


    @BindView(R.id.detail_symbol) TextView mSymbolView;
    @BindView(R.id.detail_price) TextView mPriceView;
    @BindView(R.id.detail_change) TextView mChangeView;
    @BindView(R.id.detail_name) TextView mNameView;

    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null){
            mSymbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, mSymbol);
        if (id == CURSOR_LOADER_ID){
            return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                    DETAIL_COLUMNS,
                    QuoteColumns.SYMBOL + " = ?" ,
                    new String[]{mSymbol},
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            mSymbolView.setText(mSymbol);
            mPriceView.setText(data.getString(COL_BIDPRICE));
            mChangeView.setText(data.getString(COL_CHANGE));
            mNameView.setText(data.getString(COL_NAME));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
