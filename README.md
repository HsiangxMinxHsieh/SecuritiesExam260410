# 📈 臺灣證券交易所股票資訊 App (TWSE Tracker)

這是一個基於 Android 現代化架構開發的股票資訊展示應用，透過串接 **臺灣證券交易所 OpenAPI** 獲取即時資料。本專案不僅達成面試規格要求，更針對金融數據的呈現精度與使用者流暢體驗進行了深度優化。

## 📺 專案演示 (Demo)
*   **YouTube 影片連結：** [點擊觀看 App 操作演示](https://youtu.be/UqLKz2Pj-No)
*   **核心展示內容：** 
    *   數據非同步加載與無限捲動 (Infinite Scrolling)。
    *   精確的數值對齊與金融色彩邏輯 (紅漲綠跌)。
    *   自適應不同設備螢幕比例與狀態列高度。

---

## 🛠 技術亮點 (Technical Highlights)

### 1. 金融級數據呈現 (Financial Data Precision)
*   **等寬字體對齊 (Monospace Alignment)**：
    針對股票詳情對話框 (AlertDialog)，實作了 `applyMonospace()` 擴充函式。金融數據（如本益比、殖利率）透過 `Typeface.MONOSPACE` 與 `%6.2f` 格式化處理，解決了不同數字寬度導致的對齊偏差，提供極致專業的閱讀體驗。
*   **動態色彩邏輯**：
    嚴格執行規格要求：**收盤價高於月平均價顯示紅字**，低於則顯示綠字。此外，我也額外實作了開盤價與漲跌價差的染色邏輯，提升資訊判讀效率。

### 2. 優化滑動與效能 (High-Performance UI)
*   **智慧預載機制 (Smart Pagination)**：
    在 `RecyclerView` 滑動監聽中實作了預判邏輯，當滑動至剩餘最後 10 筆資料時自動觸發 `fetchStockData()`。這種「無感加載」機制有效減少了使用者等待感。
*   **按需計算 (On-Demand Calculation)**：
    將複雜的字串格式化（如 `formatAsMetric`）封裝於點擊事件中。這確保了在 `onBindViewHolder` 快速滑動時不會進行多餘的運算，維持 60 FPS 的流暢度。
*   **點擊防抖 (Throttle Click)**：
    使用 `clickWithTrigger` 避免使用者快速重複點擊導致對話框重疊彈出，提升 App 穩定性。

### 3. 適配與穩定性 (Adaptability & Robustness)
*   **沉浸式佈局適配**：
    透過 `getStatusBarHeight()` 動態計算不同裝置的狀態列高度，並針對第一筆資料動態設定頂部 Margin，確保 UI 在各種瀏海屏設備上皆能精確對齊。
*   **自適應文字大小**：
    實作 `resetLayoutTextSize()`，根據設備螢幕比例自動微調文字大小，確保在高解析度或小螢幕裝置上皆有良好的 UI 比例。
*   **強健的空值處理**：
    實作 `emptyToDash()` 擴充函式，優雅處理 API 可能回傳的空字串或異常值，將其統一顯示為「-」。

---

## 📋 功能實作清單 (Feature Checklist)

- [x] **API 整合**：非同步獲取 TWSE 全市場股票資訊。
- [x] **動態視覺反饋**：收盤價 vs 月平均價、漲跌價差之紅綠字邏輯。
- [x] **詳情資訊 Alert**：點擊卡片精確顯示本益比、殖利率（%）、股價淨值比。
- [x] **高級篩選排序**：支援多項金融指標之升降序切換。
- [x] **螢幕轉向支援**：透過 ViewModel 保持數據狀態，旋轉不丟失資料。

---

## 🛠 開發工具與套件 (Tech Stack)
- **Language**: Kotlin 1.8+
- **Architecture**: MVVM + Repository Pattern
- **Async**: Coroutines + lifecycleScope (Flow/Launch)
- **UI Components**: ViewBinding, Material Design 3, RecyclerView
- **Networking**: Retrofit 2 + OkHttp

---

## 📂 專案結構與模組說明

本專案採用多模組架構設計，落實關注點分離 (Separation of Concerns)：

*   **`SplashActivity` & `SplashViewModel`**：
    *   **啟動流程管理**：負責 App 初始化作業，透過 ViewModel 處理非同步的預載邏輯或環境檢查。
    *   **體驗優化**：利用啟動頁面確保核心資料在進入主畫面前已完成配置，提供更流暢的進入體驗。
*   **`MainFragment.kt`**：負責核心 UI 渲染、滾動監聽（預載機制）、與沉浸式頂部動態佈局計算。
*   **`MainViewModel.kt`**：處理業務邏輯、高效能排序演算法與資料流狀態管理。
*   **`ViewBindingAdapter`**：自定義高效能的 RecyclerView 適配器封裝，降低重複代碼並提升維護性。
*   **核心模組化設計**：
    *   **`:GetNetAPILibs`**：封裝網路請求邏輯，確保資料獲取層的獨立性。
    *   **`:Base`**：定義全域通用的 UI 元件基礎類與共用工具工具集。
    *   **`:RoomLibs` / `:DatastoreLibs`**：模組化本地緩存與持久化數據管理。
---

### 如何執行本專案
1. 使用 **Android Studio Meerkat Feature Drop | 2024.3.2 Patch 1** 或更新版本開啟。
2. 確保設備具備網際網路連線以存取 OpenAPI。
3. 點擊 **Run** 即可安裝至模擬器或實體手機。

---

**如果您對專案中的技術細節（如 Monospace 實現原理或預載演算法）感興趣，歡迎在面試中與我討論！**
