# 4. 解決方案策略

## 4.1 技術選擇

| 決策領域 | 選擇 | 替代方案 | 選擇理由 |
|----------|------|---------|---------|
| 開發平台 | 各平台 Native（Android: Kotlin / iOS: Swift） | Flutter / React Native | 需要低延遲音訊存取，跨平台框架限制多；各自 Native 可最佳化 |
| UI 框架 | Jetpack Compose | XML Layout | 現代宣告式 UI，更適合即時資料展示 |
| 音訊分析 | TarsosDSP | Android AudioEffect | 開源、功能完整、社群活躍 |
| 聲音分類 | YAMNet (TFLite) | 自訓模型 / AudioSet | 預訓練 521 類聲音，免標註資料，離線可用 |
| 網路通訊 | Ktor WebSocket | OkHttp / Socket.IO | 純 Kotlin、Coroutine 原生支援 |
| 相機控制 | CameraX | Camera2 API | 簡化 API、向後相容性佳 |
| 架構模式 | MVVM | MVI / MVP | Android 官方推薦，與 Compose 整合度最高 |
| 依賴管理 | Version Catalog | buildSrc / 常數 | Gradle 官方推薦，IDE 支援度佳 |

## 4.2 架構模式

### MVVM + Clean Architecture（簡化版）

```
View (Compose UI)
    ↕ StateFlow / Event
ViewModel
    ↕ Use Case（可選）
Repository / Service
    ↕
Data Source (AudioRecord, CameraX, WebSocket, DataStore)
```

### 關鍵設計原則

1. **單向資料流（Unidirectional Data Flow）：** UI 透過 StateFlow 觀察狀態，透過 Event 傳遞使用者操作
2. **關注點分離：** 音訊處理、ML 推論、閃光控制各自獨立模組
3. **Coroutine-first：** 所有非同步操作使用 Kotlin Coroutine，避免 callback hell
4. **離線優先：** 核心功能不依賴網路（多機聯動除外）
5. **跨平台協作：** Android 與 iOS 裝置可透過共通 WebSocket 協定混合組成多機聯動網路

## 4.3 關鍵設計決策

### 音訊處理管線設計

採用**串流式管線架構**，各階段透過 Kotlin Flow 連接：

```
AudioRecord (PCM)
    → RMS 計算 (音量)
    → FFT (頻譜)
    → YAMNet (分類)
    → 觸發決策引擎
```

**理由：**
- Flow 的 backpressure 處理避免音訊緩衝區溢位
- 各階段可獨立測試
- 可依需求調整管線組合（例如僅音量偵測模式）

### 多機通訊架構

採用 **Star topology**（星狀拓撲）：主裝置為 WebSocket Server，從裝置為 Client。

**理由：**
- 架構簡單，一台裝置負責偵測與決策
- 避免多機同時偵測的衝突問題
- 從裝置僅需接收指令並觸發閃光，邏輯簡單

### 觸發條件 AND 邏輯

所有觸發條件必須同時滿足：

| 條件 | 預設閾值 | 可調整 |
|------|---------|--------|
| 音量超過閾值 | 85 dB | 是 |
| YAMNet 引擎/摩托車類信心度 | > 70% | 是 |
| 持續時間 | > 0.3 秒 | 是 |
| 在設定時段內 | 22:00 ~ 05:00 | 是 |

**理由：** AND 邏輯大幅降低誤觸發率（單一條件容易被雷聲、喇叭聲等觸發）

---

[<< 系統範圍與上下文](03-system-scope-and-context.md) | [目錄](00-index.md) | [建構區塊視圖 >>](05-building-block-view.md)
