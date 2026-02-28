# 7. 部署視圖

## 7.1 單機部署（最小配置）

```mermaid
graph TB
    subgraph 路邊安裝點
        subgraph PHONE[Android 手機]
            APP[Flashback App<br/>偵測 + 閃光]
            MIC[內建麥克風]
            FLASH[內建閃光燈]
            CAM[內建相機]
        end
        MOUNT[固定支架]
        POWER[充電線 + 行動電源]
    end

    ROAD[🛣️ 道路] -->|聲波| MIC
    FLASH -->|閃光朝向側牆反射| ROAD
    PHONE --- MOUNT
    POWER --> PHONE
```

**適用場景：** 個人測試、單一監控點

**硬體需求：**
- Android 手機 × 1（Android 7.0+）
- 固定支架
- 充電線（長時間運作需持續供電）

## 7.2 多機聯動部署（建議配置）

```mermaid
graph TB
    subgraph WiFi 區域網路
        subgraph 主裝置
            MAIN[📱 主手機<br/>Flashback Server Mode<br/>偵測 + 閃光 + WebSocket Server]
        end

        subgraph 從裝置群
            S1[📱 從手機 A<br/>Flashback Client Mode<br/>聯動閃光]
            S2[📱 從手機 B<br/>Flashback Client Mode<br/>聯動閃光]
            S3[📱 從手機 C<br/>Flashback Client Mode<br/>聯動閃光]
        end

        ROUTER[📶 WiFi 路由器<br/>或手機熱點]
    end

    MAIN <-->|WebSocket| ROUTER
    ROUTER <-->|WebSocket| S1
    ROUTER <-->|WebSocket| S2
    ROUTER <-->|WebSocket| S3
```

**適用場景：** 社區聯防、多角度嚇阻

**硬體需求：**
- 主手機 × 1（偵測 + 閃光）
- 從手機 × 2~3（聯動閃光）
- 同一 WiFi 網路或手機熱點
- 各裝置配備固定支架與供電

## 7.3 進階部署（最強嚇阻）[OPTIONAL]

```mermaid
graph TB
    subgraph WiFi 區域網路
        MAIN[📱 主手機<br/>Flashback Server]
        S1[📱 從手機 A]
        S2[📱 從手機 B]
        ESP[🔌 ESP32<br/>MicroPython<br/>WebSocket Client]
        EXT[💡 攝影棚閃光燈<br/>大功率 LED]

        MAIN <-->|WebSocket| S1
        MAIN <-->|WebSocket| S2
        MAIN <-->|WebSocket| ESP
        ESP -->|GPIO 控制| EXT
    end

    BOX[🔲 防水盒<br/>戶外安裝]
    ESP --- BOX
    EXT --- BOX
```

**額外硬體：**
- ESP32 開發板 × 1（約 NT$200）
- 攝影棚閃光燈或大功率 LED 頻閃燈 × 1~2（NT$1,000~3,000）
- 防水盒（戶外安裝用）

## 7.4 跨平台混合部署 [PLANNED]

```mermaid
graph TB
    subgraph WiFi 區域網路
        subgraph Android
            MAIN[📱 Android 主手機<br/>Flashback Server<br/>偵測 + 閃光]
            SA[📱 Android 從機]
        end
        subgraph iOS
            SI1[📱 iPhone 從機 A<br/>Flashback iOS Client<br/>聯動閃光]
            SI2[📱 iPhone 從機 B<br/>Flashback iOS Client<br/>聯動閃光]
        end
        MAIN <-->|WebSocket JSON| SA
        MAIN <-->|WebSocket JSON| SI1
        MAIN <-->|WebSocket JSON| SI2
    end
```

**說明：** Android 與 iOS 裝置透過共通的 WebSocket JSON 協定通訊，iOS 裝置可作為從機加入聯動閃光網路。主裝置目前僅支援 Android（因 Ktor WebSocket Server）。

## 7.5 開發環境

```mermaid
graph LR
    subgraph 開發者機器
        subgraph Docker Container
            UBUNTU[Ubuntu 22.04]
            JDK[OpenJDK 17]
            GRADLE[Gradle 8.11.1]
            SDK[Android SDK 35]
        end
        VSCODE[VS Code<br/>+ Kotlin Extension<br/>+ Gradle Extension]
        VSCODE --> Docker Container
    end

    subgraph 測試
        EMU[Android Emulator<br/>或實體手機]
    end

    Docker Container -->|./gradlew assembleDebug| APK[Debug APK]
    APK -->|adb install| EMU
```

**開發環境規格：**

| 元件 | 版本 |
|------|------|
| 容器基礎 | Ubuntu 22.04 |
| JDK | OpenJDK 17 |
| Gradle | 8.11.1 |
| Android SDK | Platform 35, Build Tools 35.0.0 |
| IDE 擴充套件 | fwcd.kotlin, vscjava.vscode-gradle |

## 7.6 iOS 開發環境 [PLANNED]

```mermaid
graph LR
    subgraph macOS 主機 Apple Silicon
        VSCODE[VS Code] -->|SSH Remote| TART_VM

        subgraph TART_VM[Tart macOS VM]
            MACOS[macOS Guest]
            XCODE[Xcode + Swift Toolchain]
            IOS_SDK[iOS SDK]
        end
    end

    subgraph 測試
        SIM[iOS Simulator<br/>或實體 iPhone]
    end

    TART_VM -->|xcodebuild| IPA[Debug Build]
    IPA -->|安裝| SIM
```

**環境說明：**

| 元件 | 說明 |
|------|------|
| Host | macOS（Apple Silicon） |
| VM 工具 | [Tart](https://tart.run/)（Apple Virtualization.framework） |
| Guest OS | macOS（與 Host 相同或相近版本） |
| IDE | VS Code + SSH Remote（連線至 VM） |
| 建置工具 | Xcode + xcodebuild |

> 與 Android devcontainer 的開發體驗一致：開發者在 VS Code 中編輯程式碼，建置與執行在遠端環境（Docker 容器 / Tart VM）完成。唯一差異是 iOS 開發需在 macOS 上載入 VS Code。

---

[<< 執行期視圖](06-runtime-view.md) | [目錄](00-index.md) | [橫切關注點 >>](08-crosscutting-concepts.md)
