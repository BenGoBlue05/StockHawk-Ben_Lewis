package com.sam_chordas.android.stockhawk.service;

import com.sam_chordas.android.stockhawk.rest.StockHistory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by bplewis5 on 8/24/16.
 */
public final class StockHistoryService {

    public interface StockHistoryAPI{
        @GET("v1/public/yql?&format=json&diagnostics=true&env=store://datatables.org/alltableswithkeys&callback=")
        Call<StockHistory> getHistory(
                @Query("q") String query);
    }
}

/*
https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%27YHOO%27%20and%20startDate%20%3D%20%272016-07-19%27%20and%20endDate%20%3D%20%272016-07-26%27&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
*/