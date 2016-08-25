package com.sam_chordas.android.stockhawk.rest;

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

    private Query query;

    public StockHistory(Query query) {
        this.query = query;
    }

    public int getCount() {
        return query.getCount();
    }

    public double[] getClosingPrices(int count) {
        double[] closingPrices = new double[count];
        List<Quote> quotes = query.getResults().getQuotes();
        for (int i = 0; i < count; i++) {
            double price = quotes.get(i).getClose();
            closingPrices[i] = Double.parseDouble(String.format(Locale.US, "%.2f", price));
        }
        return closingPrices;
    }

    public String[] getDates(int count) {
        String[] dates = new String[count];
        List<Quote> quotes = query.getResults().getQuotes();
        for (int i = 0; i < count; i++) {
            dates[i] = quotes.get(i).getDate();
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
