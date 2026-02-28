# 6. 執行期視圖

## 6.1 場景一：聲音偵測與閃光觸發（單機）

```mermaid
sequenceDiagram
    participant ENV as 環境聲音
    participant AR as AudioRecord
    participant RMS as RMS Calculator
    participant FFT as FFT Processor
    participant YN as YAMNet
    participant TE as Trigger Engine
    participant FL as Flash Controller
    participant LOG as Event Logger

    ENV->>AR: 聲波輸入
    AR->>RMS: PCM 串流
    AR->>FFT: PCM 串流
    AR->>YN: PCM 串流（0.975s 窗口）

    RMS->>TE: 音量 92dB ✓
    YN->>TE: motorcycle_engine 85% ✓
    TE->>TE: 持續 0.5s ✓
    TE->>TE: 時間 01:30 ✓
    TE->>TE: AND 全部通過 → 觸發！

    TE->>FL: triggerFlash()
    Note over FL: < 100ms 延遲
    FL->>FL: 閃光燈開 200ms
    TE->>LOG: 記錄事件
```

## 6.2 場景二：多機聯動閃光

```mermaid
sequenceDiagram
    participant TE as 主裝置<br/>Trigger Engine
    participant FL1 as 主裝置<br/>Flash
    participant WS as WebSocket Server
    participant C1 as 從裝置 A
    participant C2 as 從裝置 B

    TE->>FL1: triggerFlash()
    Note over FL1: 本機閃光（< 100ms）
    TE->>WS: broadcastTrigger()

    par 廣播至所有從裝置
        WS->>C1: {"type":"trigger","timestamp":...}
        WS->>C2: {"type":"trigger","timestamp":...}
    end

    Note over C1: 接收延遲 200~500ms
    C1->>C1: 閃光燈開 200ms
    C2->>C2: 閃光燈開 200ms

    Note over FL1,C2: 多點閃光製造「多處測速照相」效果
```

## 6.3 場景三：應用程式啟動流程

```mermaid
sequenceDiagram
    participant USER as 使用者
    participant APP as MainActivity
    participant PERM as Permission Manager
    participant SVC as Audio Service
    participant WS as WebSocket Server
    participant UI as Compose UI

    USER->>APP: 啟動 App
    APP->>PERM: 檢查權限
    PERM->>USER: 請求 RECORD_AUDIO
    USER->>PERM: 授權 ✓
    PERM->>USER: 請求 CAMERA
    USER->>PERM: 授權 ✓

    APP->>SVC: 啟動音訊監聽
    APP->>WS: 啟動 WebSocket Server
    APP->>UI: 顯示主控介面

    loop 持續監聽
        SVC->>UI: 更新音量 / 頻譜
    end
```

## 6.4 場景四：非飆車聲音（不觸發）

```mermaid
sequenceDiagram
    participant ENV as 環境聲音
    participant RMS as RMS Calculator
    participant YN as YAMNet
    participant TE as Trigger Engine

    Note over ENV: 大卡車經過（引擎聲但非飆車）

    RMS->>TE: 音量 78dB ✗（< 85dB）
    YN->>TE: truck 60% / motorcycle 15%

    TE->>TE: 音量未達閾值 → 不觸發
    Note over TE: AND 邏輯：任一條件不滿足即不觸發

    Note over ENV: 喇叭聲（短促高音量）

    RMS->>TE: 音量 95dB ✓
    YN->>TE: vehicle_horn 90% / motorcycle 5% ✗

    TE->>TE: YAMNet 信心度不足 → 不觸發
```

---

[<< 建構區塊視圖](05-building-block-view.md) | [目錄](00-index.md) | [部署視圖 >>](07-deployment-view.md)
