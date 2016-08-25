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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.StockHistory;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.StockHistoryService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_ID = 0;
    public static final int COL_SYMBOL = 1;
    public static final int COL_BIDPRICE = 2;
    public static final int COL_PERCENT_CHANGE = 3;
    public static final int COL_CHANGE = 4;
    public static final int COL_NAME = 5;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String API_BASE_URL = "https://query.yahooapis.com/";
    private static final int CURSOR_LOADER_ID = 100;

    private static final String[] DETAIL_COLUMNS = new String[]{
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.CHANGE,
            QuoteColumns.NAME};

    @BindView(R.id.detail_symbol)
    TextView mSymbolView;
    @BindView(R.id.detail_price)
    TextView mPriceView;
    @BindView(R.id.detail_change)
    TextView mChangeView;
    @BindView(R.id.detail_name)
    TextView mNameView;
    @BindView(R.id.line_chart)
    LineChart mLineChart;

    private String mSymbol;
    private ArrayList<Entry> mClosingPrices;
    private ArrayList<String> mDates;
    private int mCount;


    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mSymbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Log.i(LOG_TAG, String.format(getString(R.string.query), "a", "b", "c"));
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        fetchStockHistory(5);
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, mSymbol);
        if (id == CURSOR_LOADER_ID) {
            return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                    DETAIL_COLUMNS,
                    QuoteColumns.SYMBOL + " = ? AND " + QuoteColumns.ISCURRENT + " = ?",
                    new String[]{mSymbol, "1"},
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mSymbolView.setText(mSymbol);
            mPriceView.setText(data.getString(COL_BIDPRICE));
            mChangeView.setText(data.getString(COL_CHANGE));
            mNameView.setText(data.getString(COL_NAME));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void fetchStockHistory(int daysAgo) {

        Log.i(LOG_TAG, "DATE: " + Utils.getDate(1));
        Log.i(LOG_TAG, "DATE: " + Utils.getDate(8));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StockHistoryService.StockHistoryAPI stockHistoryAPI =
                retrofit.create(StockHistoryService.StockHistoryAPI.class);

        String query = Utils.buildStockHistoryQuery(mSymbol, Utils.getDate(daysAgo), Utils.getDate(1));
        Call<StockHistory> serviceCall = stockHistoryAPI.getHistory(query);
        serviceCall.enqueue(new Callback<StockHistory>() {
            @Override
            public void onResponse(Call<StockHistory> call, Response<StockHistory> response) {
                Log.i(LOG_TAG, "SUCCESS");
                StockHistory history = response.body();
                mCount = history.getCount();
                mDates = history.getDates(mCount);
                mClosingPrices = history.getClosingPrices(mCount);
                Log.i(LOG_TAG, "COUNT: " + mCount);
                updateLineChart();
            }

            @Override
            public void onFailure(Call<StockHistory> call, Throwable t) {
                Log.i(LOG_TAG, "FAILURE");
            }
        });
    }

    public void updateLineChart() {
        LineDataSet lineDataSet = new LineDataSet(mClosingPrices, "$");
        LineData lineData = new LineData(mDates, lineDataSet);
        mLineChart.setData(lineData);
    }

}
