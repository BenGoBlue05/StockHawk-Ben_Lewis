package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;


public class StockRemoteViewService extends RemoteViewsService {

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.CHANGE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.ISUP
    };

    private static final int INDEX_ID = 0;
    private static final int INDEX_SYMBOL = 1;
    private static final int INDEX_PRICE = 2;
    private static final int INDEX_CHANGE = 3;
    private static final int INDEX_PERCENT_CHANGE = 4;
    private static final int INDEX_IS_UP = 5;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long idToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        QUOTE_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(idToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }


                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list_item);

                String symbol = data.getString(INDEX_SYMBOL);
                remoteViews.setTextViewText(R.id.widget_symbol_textview, symbol);
                remoteViews.setContentDescription(R.id.widget_symbol_textview,
                        getString(R.string.a11y_symbol, symbol));

                String price = data.getString(INDEX_PRICE);
                remoteViews.setTextViewText(R.id.widget_price_textview, price);
                remoteViews.setContentDescription(R.id.widget_price_textview, price);

                String change = data.getString(INDEX_CHANGE);
                remoteViews.setTextViewText(R.id.widget_change_textview, change);
                remoteViews.setContentDescription(R.id.widget_change_textview, change);

                Uri uri = QuoteProvider.Quotes.withSymbol(symbol);
                Intent intent = new Intent()
                        .setData(uri)
                        .putExtra(Intent.EXTRA_TEXT, symbol)
                        .putExtra("tag", "historical");

                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, intent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getInt(INDEX_ID);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }


}
