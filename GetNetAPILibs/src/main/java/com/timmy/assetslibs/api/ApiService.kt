package com.timmy.assetslibs.api

import com.timmy.base.data.response.BBUData
import com.timmy.base.data.response.StockAVGData
import com.timmy.base.data.response.StockData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {

    @Headers("Accept: application/json")
    @GET("v1/exchangeReport/BWIBBU_ALL")
    suspend fun getBbuData(): Response<BBUData>

    @Headers("Accept: application/json")
    @GET("v1/exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockAvg(): Response<StockAVGData>

    @Headers("Accept: application/json")
    @GET("v1/exchangeReport/STOCK_DAY_ALL")
    suspend fun getStock(): Response<StockData>

}