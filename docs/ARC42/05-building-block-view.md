# 5. 建構區塊視圖

## 5.1 Level 0 — 系統整體

```mermaid
graph TB
    ENV[環境聲音] --> FB[Flashback 系統]
    FB --> FLASH[閃光輸出]
    FB --> LOG[事件記錄]
    USER[使用者] --> FB
    FB -.-> NOTIFY[通知 選配]
```

Flashback 系統接收環境聲音輸入，經過分析後決定是否觸發閃光輸出與事件記錄。

## 5.2 Level 1 — 主要元件

```mermaid
graph TB
    subgraph 主裝置
        UI[UI Layer<br/>Jetpack Compose]
        VM[ViewModel Layer]
        AUDIO[Audio Service<br/>音訊擷取與分析]
        ML[ML Engine<br/>YAMNet 推論]
        TRIGGER[Trigger Engine<br/>觸發決策]
        FLASH_CTRL[Flash Controller<br/>閃光燈控制]
        CAM[Camera Service<br/>拍照]
        WS[WebSocket Server<br/>多機通訊]
        STORE[Data Store<br/>設定與記錄]
    end

    UI <--> VM
    VM --> AUDIO
    VM --> FLASH_CTRL
    VM --> WS
    VM --> STORE

    AUDIO --> ML
    AUDIO --> TRIGGER
    ML --> TRIGGER
    TRIGGER --> FLASH_CTRL
    TRIGGER --> CAM
    TRIGGER --> WS
    TRIGGER --> STORE
```

### 元件職責

| 元件 | 職責 | 狀態 |
|------|------|------|
| UI Layer | 顯示即時音量、頻譜、系統狀態；提供設定介面 | [IMPLEMENTED] 僅 scaffold |
| ViewModel Layer | 管理 UI 狀態、協調各 Service | [PLANNED] |
| Audio Service | 透過 AudioRecord 擷取 PCM 串流、計算 RMS、執行 FFT | [PLANNED] |
| ML Engine | 載入 YAMNet TFLite 模型、執行聲音分類推論 | [PLANNED] |
| Trigger Engine | 整合多重條件（AND 邏輯）判斷是否觸發 | [PLANNED] |
| Flash Controller | 控制手機閃光燈開關 | [PLANNED] |
| Camera Service | 觸發時拍照存檔 | [OPTIONAL] |
| WebSocket Server | 廣播觸發事件給從裝置 | [PLANNED] |
| Data Store | 持久化使用者設定與觸發記錄 | [PLANNED] |

## 5.3 Level 2 — 音訊處理管線（核心元件）

```mermaid
graph LR
    subgraph Audio Service
        AR[AudioRecord<br/>PCM 16-bit<br/>16kHz mono]
        BUF[Ring Buffer<br/>音訊緩衝區]
    end

    subgraph Analysis Pipeline
        RMS[RMS Calculator<br/>音量 dB]
        FFT[FFT Processor<br/>TarsosDSP<br/>頻譜分析]
        MEL[Mel Spectrogram<br/>YAMNet 前處理]
    end

    subgraph ML Engine
        YAMNET[YAMNet TFLite<br/>521 類聲音分類]
        FILTER[Category Filter<br/>引擎/摩托車類篩選]
    end

    subgraph Trigger Engine
        VOL_CHECK[音量檢查<br/>> 85dB]
        CONF_CHECK[信心度檢查<br/>> 70%]
        DUR_CHECK[持續時間檢查<br/>> 0.3s]
        TIME_CHECK[時段檢查<br/>22:00-05:00]
        AND_GATE[AND Gate<br/>全部滿足才觸發]
    end

    AR --> BUF
    BUF --> RMS
    BUF --> FFT
    BUF --> MEL
    MEL --> YAMNET
    YAMNET --> FILTER

    RMS --> VOL_CHECK
    FILTER --> CONF_CHECK
    VOL_CHECK --> AND_GATE
    CONF_CHECK --> AND_GATE
    DUR_CHECK --> AND_GATE
    TIME_CHECK --> AND_GATE

    AND_GATE -->|觸發| ACTION[Flash + Photo + WebSocket + Log<br/>+ 外接閃光 WiFi/BLE/2.4GHz]
```

### 音訊處理規格

| 參數 | 值 | 說明 |
|------|-----|------|
| 取樣率 | 16,000 Hz | YAMNet 要求 |
| 位元深度 | 16-bit PCM | AudioRecord 標準格式 |
| 聲道 | Mono | 單聲道即可 |
| FFT 視窗 | 1024 samples | 約 64ms 一幀 |
| YAMNet 輸入 | 0.975 秒 | 15,600 samples per frame |
| 分析頻率 | ~10 FPS | 每秒約 10 次完整分析 |

---

[<< 解決方案策略](04-solution-strategy.md) | [目錄](00-index.md) | [執行期視圖 >>](06-runtime-view.md)
