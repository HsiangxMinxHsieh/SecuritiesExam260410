package com.timmy.assetslibs.repo

import com.timmy.assetslibs.api.ApiService
import com.timmy.base.baseResponse.ResultState
import com.timmy.base.data.response.BBUData
import com.timmy.base.data.response.StockAVGData
import com.timmy.base.data.response.StockData
import com.timmymike.logtool.loge
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class GetAPIRepository @Inject constructor(/*private val context: Application*/) {

    @Inject
    lateinit var retrofit: Retrofit

    private val apiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    suspend fun getBBUData(): ResultState<BBUData> {
        return safeApiCall(getCallerTag()) { apiService.getBbuData() }
    }

    suspend fun getStockAVG(): ResultState<StockAVGData> {
        return safeApiCall(getCallerTag()) { apiService.getStockAvg() }
    }

    suspend fun getStock(): ResultState<StockData> {
        return safeApiCall(getCallerTag()) { apiService.getStock() }
    }

    // 統一的API呼叫方法(包含錯誤處理)：
    private suspend fun <T> safeApiCall(
        tag: String,
        apiCall: suspend () -> Response<T>,
    ): ResultState<T> {
        return try {
            var response = apiCall()

            when {
                response.isSuccessful -> {
                    val data = response.body()
                    if (data != null) {
                        ResultState.Success(data)
                    } else {
                        loge("[呼叫:${tag}]時出現異常：資料為空")
                        ResultState.Error("[呼叫:${tag}]時出現異常：資料為空")
                    }
                }

                else -> {
                    val errorMsg = response.errorBody()?.string()
                        ?: "未知錯誤(錯誤內容為空)"
                    loge("[呼叫:${tag}]時出現異常：$errorMsg ")
                    ResultState.Error("[呼叫:${tag}]時出現異常：$errorMsg ")
                }
            }
        } catch (e: Exception) {
            loge("[呼叫:${tag}]時出現異常：網際網路錯誤，錯誤資訊：${e.message}")
            ResultState.Error("[呼叫:${tag}]時出現異常：網際網路錯誤，錯誤資訊：${e.message}")
        } as ResultState<T>
    }

    private fun getCallerTag(): String {
        val stack = Throwable().stackTrace
        // 0 -> getCallerInfo
        // 1 -> 呼叫的方法名稱
        return stack.getOrNull(1)?.methodName ?: "未知來源"
    }

}
