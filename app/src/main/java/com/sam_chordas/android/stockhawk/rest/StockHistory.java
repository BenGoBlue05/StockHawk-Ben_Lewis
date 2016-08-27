package com.sam_chordas.android.stockhawk.rest;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by bplewis5 on 8/24/16.
 */

/*
    {
    "query":{
        "count":6,
        "created":"2016-08-25T14:00:44Z",
        "lang":"en-US",
        "diagnostics":{  },
        "results":{
            "quote":[
            {
                    "Symbol":"YHOO",
                    "Date":"2016-07-26",
                    "Open":"38.07",
                    "High":"38.830002",
                    "Low":"37.900002",
                    "Close":"38.759998",
                    "Volume":"15978600",
                    "Adj_Close":"38.759998"
            },
            {  },
            {  },
            {  },
            {  },
            {  }
            ]
        }
    }
    }
 */
public class StockHistory {

    private final static String LOG_TAG = StockHistory.class.getSimpleName();
    private Query query;

    public StockHistory(Query query) {
        this.query = query;
    }

    public int getCount() {
        return query.getCount();
    }

    public ArrayList<Entry> getClosingPrices(int count) {
        ArrayList<Entry> closingPrices = new ArrayList<Entry>();
        try{
            List<Quote> quotes = query.getResults().getQuotes();
            for (int i = 0; i < count; i++) {
                double price = quotes.get(count - i - 1).getClose();
                closingPrices.add(new Entry(Float.parseFloat(String.format(Locale.US, "%.2f", price)), i));
            }
        } catch (NullPointerException e){
            Log.e(LOG_TAG, "NULL POINTER EXCEPTION");
        }
        return closingPrices;
    }

    public ArrayList<String> getDates(int count) {
        ArrayList<String> dates = new ArrayList<>();
        try{
            List<Quote> quotes = query.getResults().getQuotes();
            for (int i = 0; i < count; i++) {
                dates.add(quotes.get(count - i - 1).getDate());
            }
        } catch (NullPointerException e){
            Log.e(LOG_TAG, "NULL POINTER");
        }
        return dates;
    }

    public class Query {
        private Results results;
        private int count;

        public Query(Results results, int count) {
            this.results = results;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public Results getResults() {
            return results;
        }
    }

    public class Results {
        private List<Quote> quote;

        public Results(List<Quote> quote) {
            this.quote = quote;
        }

        public List<Quote> getQuotes() {
            return quote;
        }
    }

    public class Quote {
        private String Symbol;
        private String Date;
        private double Close;

        public Quote(String Symbol, String Date, double Close) {
            this.Symbol = Symbol;
            this.Date = Date;
            this.Close = Close;
        }

        public double getClose() {
            return Close;
        }

        public String getSymbol() {
            return Symbol;
        }

        public String getDate() {
            return Date;
        }
    }
}
