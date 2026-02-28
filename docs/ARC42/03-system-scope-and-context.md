# 3. 系統範圍與上下文

## 3.1 業務上下文

```mermaid
graph TB
    subgraph 外部環境
        SOUND[🔊 環境聲音<br/>飆車噪音 / 一般車流]
        RIDER[🏍️ 飆車族]
        RESIDENT[🏠 社區居民<br/>系統操作者]
    end

    subgraph Flashback 系統
        MAIN[📱 主裝置<br/>偵測 + 閃光]
        SLAVE1[📱 從裝置 A<br/>聯動閃光]
        SLAVE2[📱 從裝置 B<br/>聯動閃光]
    end

    SOUND -->|聲波| MAIN
    MAIN -->|⚡ 閃光嚇阻| RIDER
    MAIN -->|WebSocket| SLAVE1
    MAIN -->|WebSocket| SLAVE2
    SLAVE1 -->|⚡ 閃光| RIDER
    SLAVE2 -->|⚡ 閃光| RIDER
    RESIDENT -->|設定參數| MAIN

    ESP[🔌 ESP32 外接閃光<br/>選配]
    MAIN -.->|控制訊號| ESP
    ESP -.->|⚡ 強力閃光| RIDER

    TG[📨 Telegram Bot<br/>選配]
    MAIN -.->|觸發通知| TG
    TG -.->|通報| RESIDENT
```

### 業務上下文說明

| 通訊夥伴 | 輸入 | 輸出 |
|----------|------|------|
| 環境聲音 | PCM 音訊串流 | — |
| 飆車族 | —（被動角色） | 閃光嚇阻效果 |
| 社區居民 | 參數設定、安裝部署 | 觸發記錄、通知 |
| 從裝置 | — | WebSocket 觸發指令 |
| ESP32（選配） | — | 控制訊號 |
| Telegram（選配） | — | 觸發通知訊息 |

## 3.2 技術上下文

```mermaid
graph LR
    subgraph 主裝置 Android
        MIC[AudioRecord API] --> PIPE[音訊處理管線]
        PIPE --> ML[YAMNet TFLite]
        ML --> TRIGGER{觸發決策}
        TRIGGER -->|是| FLASH[CameraX Flash]
        TRIGGER -->|是| WS_S[Ktor WebSocket Server]
        TRIGGER -->|是| CAM[CameraX 拍照]
        TRIGGER -->|是| LOG[觸發記錄]
    end

    subgraph 從裝置 Android
        WS_C[Ktor WebSocket Client] --> FLASH2[CameraX Flash]
    end

    WS_S -->|JSON over WebSocket<br/>WiFi LAN| WS_C

    subgraph 選配
        ESP_C[ESP32 Client] --> EXT_FLASH[外接閃光燈]
        TG_BOT[Telegram Bot API]
    end

    WS_S -.->|WebSocket| ESP_C
    LOG -.->|HTTP| TG_BOT
```

### 技術介面說明

| 介面 | 技術 | 協定 | 說明 |
|------|------|------|------|
| 音訊輸入 | AudioRecord API | PCM 16-bit | 低延遲音訊串流 |
| 裝置間通訊 | Ktor WebSocket | JSON over WS | 同一 WiFi LAN 內 |
| 閃光燈控制 | CameraX API | — | Android Camera2 封裝 |
| ML 推論 | TensorFlow Lite | — | YAMNet 模型，裝置端推論 |
| 外接控制（選配） | ESP32 WebSocket | JSON over WS | MicroPython |
| 通知（選配） | Telegram Bot API | HTTPS | 推播觸發事件 |

---

[<< 架構限制](02-architecture-constraints.md) | [目錄](00-index.md) | [解決方案策略 >>](04-solution-strategy.md)
