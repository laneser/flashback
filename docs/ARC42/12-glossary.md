# 12. 術語表

| 中文術語 | 英文原文 | 說明 |
|----------|---------|------|
| 飆車 | Street racing | 非法在公共道路上高速行駛的行為 |
| 炸街 | Rev bombing / Loud exhaust | 改裝排氣管製造極大噪音的行為 |
| 嚇阻 | Deterrence | 透過閃光讓飆車族以為被拍照，達到威嚇效果 |
| 主裝置 | Primary device / Server | 負責偵測與決策的 Android 手機 |
| 從裝置 | Secondary device / Client | 僅負責聯動閃光的 Android / iOS 手機 |
| 觸發 | Trigger | 偵測到飆車聲音後啟動閃光等動作 |
| 誤觸發 | False positive | 非飆車聲音被錯誤判定為飆車而觸發 |
| 音訊管線 | Audio pipeline | 從音訊擷取到分析結果的資料處理流程 |
| 頻譜分析 | Spectrum analysis | 將時域音訊信號轉換為頻域，分析各頻率成分 |
| 信心度 | Confidence score | ML 模型對分類結果的確信程度（0~1） |
| 閾值 | Threshold | 觸發條件的臨界值 |
| 星狀拓撲 | Star topology | 一個中心節點連接多個端節點的網路拓撲 |
| 前景服務 | Foreground Service | Android 不會在背景殺死的服務類型 |
| 版本目錄 | Version Catalog | Gradle 集中管理依賴版本的機制 |
| — | ADR | Architecture Decision Record，架構決策紀錄 |
| — | AVAudioEngine | iOS 原生音訊處理引擎 |
| — | AVCaptureDevice | iOS 相機與閃光燈控制 API |
| — | AGP | Android Gradle Plugin |
| — | AND 邏輯 | 所有條件必須同時滿足才觸發 |
| — | AudioRecord | Android 低階音訊錄製 API |
| — | BOM | Bill of Materials，依賴版本清單 |
| — | CameraX | Android Jetpack 相機 API 封裝 |
| — | Compose | Jetpack Compose，Android 宣告式 UI 框架 |
| — | Core ML | Apple 裝置端 ML 推論框架 |
| — | Coroutine | Kotlin 協程，輕量級並行處理機制 |
| — | dB (Decibel) | 分貝，聲音強度單位 |
| — | ESP32 | Espressif 低成本 WiFi/BLE 微控制器 |
| — | FFT | Fast Fourier Transform，快速傅立葉轉換 |
| — | Flow | Kotlin Flow，冷串流非同步資料流 |
| — | Ktor | 純 Kotlin 非同步網路框架 |
| — | MVVM | Model-View-ViewModel 架構模式 |
| — | PCM | Pulse-Code Modulation，脈衝碼調變（數位音訊格式） |
| — | RMS | Root Mean Square，均方根（音量計算方式） |
| — | SwiftUI | Apple 宣告式 UI 框架（對應 Android 的 Jetpack Compose） |
| — | StateFlow | Kotlin 狀態流，具有當前值的熱串流 |
| — | TarsosDSP | Java/Android 音訊分析函式庫 |
| — | TFLite | TensorFlow Lite，行動裝置 ML 推論框架 |
| — | WebSocket | 全雙工即時通訊協定 |
| — | Tart | Apple Silicon 原生 macOS 虛擬機工具（基於 Virtualization.framework） |
| — | YAMNet | Yet Another Mobile Network，Google 聲音分類模型 |

---

[<< 風險與技術債](11-risks-and-technical-debts.md) | [目錄](00-index.md)
