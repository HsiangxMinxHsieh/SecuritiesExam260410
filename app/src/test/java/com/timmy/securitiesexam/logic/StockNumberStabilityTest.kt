package com.timmy.securitiesexam.logic

import com.timmy.base.cons.GlobalConst
import org.junit.Assert.assertEquals
import org.junit.Test

class StockNumberStabilityTest {
    private val processor = AStockDataProcessor()

    // -------------------------------------------------------------------------
    //  穩定性與異常處理測試
    // -------------------------------------------------------------------------
    @Test
    fun `測試非法資料轉換不應崩潰`() {

        // 測試null、空字串、雙橫線，各種數字。
        val inputs = listOf(null, "", "-", "--", "x", "null", "N/A", "8,352,345.67")

        inputs.forEach { input ->
            try {

                val result = with(processor) { input.toSafeDouble() }

                if (input == "8,352,345.67") {
                    assertEquals(8352345.67, result, 0.0)
                } else {
                    assertEquals(GlobalConst.EMPTY_DATA_VALUE, result, 0.0)
                }
            } catch (e: Exception) {
                org.junit.Assert.fail("輸入為 $input 時發生崩潰: ${e.message}")
            }
        }
    }
}