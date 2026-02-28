# 9. 架構決策紀錄（ADR）

## ADR-001：各平台 Native 而非跨平台框架

**狀態：** 已決定（已更新：新增 iOS Native 規劃）

**背景：** 需要低延遲音訊存取（< 100ms）與硬體直接控制（閃光燈、相機）。

**選項：**
1. 各平台 Native（Android: Kotlin / iOS: Swift）
2. Flutter
3. React Native
4. Kotlin Multiplatform（KMP）

**決策：** 各平台 Native

**理由：**
- Android: AudioRecord API 提供最低延遲的 PCM 串流存取
- iOS: AVAudioEngine 提供原生低延遲音訊處理
- 各平台直接存取相機 / 閃光燈 API，無跨平台橋接開銷
- TensorFlow Lite / Core ML 在各自 Native 環境效能最佳
- 各平台可獨立最佳化，不受跨平台框架限制

**後果：** 需維護兩套程式碼（Kotlin + Swift），但核心邏輯相似，透過共通 WebSocket JSON 協定實現跨平台多機協作

---

## ADR-002：YAMNet 預訓練模型而非自訓練模型

**狀態：** 已決定

**背景：** 需要辨識飆車聲音（引擎聲、摩托車聲、輪胎聲、爆鳴聲）。

**選項：**
1. Google YAMNet（預訓練 521 類聲音）
2. 自行收集資料訓練專用模型
3. AudioSet + 自訂分類器

**決策：** YAMNet 預訓練模型

**理由：**
- 已涵蓋 motorcycle、engine、tire squeal 等目標類別
- 免除資料收集與標註的大量工作
- TFLite 格式，離線推論效能佳
- Google 維護，模型品質有保障

**風險：** YAMNet 分類粒度可能不足（例如無法區分「正常摩托車」與「飆車摩托車」），未來可能需要 fine-tuning 或補充自訓模型

---

## ADR-003：Ktor WebSocket 而非其他網路框架

**狀態：** 已決定

**背景：** 多機聯動需要低延遲的即時通訊機制。

**選項：**
1. Ktor WebSocket
2. OkHttp WebSocket
3. Socket.IO
4. gRPC

**決策：** Ktor WebSocket

**理由：**
- 純 Kotlin 實作，與專案技術棧一致
- 原生 Coroutine 支援，無需 callback 轉換
- 可同時作為 Server 與 Client
- 輕量級，適合手機端運作

---

## ADR-004：星狀拓撲（Star Topology）多機架構

**狀態：** 已決定

**背景：** 多台手機需要同步閃光，需決定通訊拓撲。

**選項：**
1. 星狀拓撲（一台主機 Server + 多台從機 Client）
2. 網狀拓撲（Mesh，每台設備皆可偵測與觸發）
3. P2P（Wi-Fi Direct）

**決策：** 星狀拓撲

**理由：**
- 僅一台設備負責偵測，避免多機同時偵測的衝突與判定問題
- 從機邏輯極簡（接收指令 → 閃光），降低開發複雜度
- WebSocket Server/Client 模型直覺易懂

**後果：** 主機單點故障會導致所有從機失效，但對嚇阻系統而言影響可接受

---

## ADR-005：AND 邏輯觸發條件

**狀態：** 已決定

**背景：** 需要在即時偵測與低誤觸發率之間取得平衡。

**選項：**
1. AND 邏輯（所有條件同時滿足才觸發）
2. OR 邏輯（任一條件滿足即觸發）
3. 加權評分（各條件加權後超過總分閾值觸發）

**決策：** AND 邏輯

**理由：**
- 大幅降低誤觸發（雷聲、喇叭聲、施工聲等）
- 實作簡單，易於理解與除錯
- 各條件閾值可獨立調整
- 犧牲部分召回率換取高精確率，對嚇阻系統更重要

---

## ADR-006：Foreground Service 持續運作

**狀態：** 已決定

**背景：** 系統需要在螢幕關閉時持續監聽音訊。

**選項：**
1. Foreground Service + PARTIAL_WAKE_LOCK
2. WorkManager（週期性排程）
3. AlarmManager + 短暫喚醒

**決策：** Foreground Service

**理由：**
- 唯一能保證持續音訊監聽的方式
- Android 系統不會殺死帶有 persistent notification 的 Foreground Service
- 雖然耗電較高，但系統設計本就預期持續供電（充電線）

---

## ADR-007：Jetpack Compose 而非 XML Layout

**狀態：** 已決定

**背景：** 需要即時顯示音量、頻譜等動態資料的 UI 框架。

**選項：**
1. Jetpack Compose
2. 傳統 XML Layout + View Binding

**決策：** Jetpack Compose

**理由：**
- 宣告式 UI 更適合即時資料更新（音量表、頻譜圖）
- 與 StateFlow/State 整合度高，減少 boilerplate
- Material 3 元件完整，設定介面開發效率高
- Android 官方主推方向，長期維護有保障

---

## ADR-008：外接閃光燈三方案並行

**狀態：** 已決定

**背景：** 手機內建閃光燈亮度有限，需要外接大功率閃光燈以增強嚇阻效果。市面閃光燈的無線控制介面各異。

**選項：**
1. WiFi 直連（閃光燈內建 WiFi）
2. Bluetooth 直連（閃光燈內建 BLE）
3. 2.4GHz 經 ESP32 橋接（Phone → BT/WiFi → ESP32 → 2.4GHz → Flash）
4. 僅支援單一方案

**決策：** 三方案並行支援，以 Plugin 模式實作

**理由：**
- WiFi 方案延遲低、無額外硬體，適合支援 WiFi 遙控的閃光燈
- BLE 方案省電、配對簡單，適合近距離部署
- 2.4GHz 方案可驅動攝影棚級閃光燈，閃光功率最強，嚇阻效果最佳
- 三種方案覆蓋不同價位與場景的閃光燈設備
- Plugin 架構使未來新增其他控制方式（如 Zigbee）容易擴充

**後果：** 需實作三種通訊協定的抽象層，增加開發量但提高設備相容性

---

## ADR-009：iOS 開發環境使用 Tart macOS VM

**狀態：** 已決定

**背景：** iOS 開發需要 macOS + Xcode，但希望維持與 Android devcontainer 一致的遠端開發體驗。

**選項：**
1. Tart macOS VM + VS Code SSH Remote
2. 直接在 macOS 本機用 Xcode 開發
3. GitHub Actions macOS runner（CI-only）

**決策：** Tart macOS VM + VS Code SSH Remote

**理由：**
- Tart 使用 Apple Virtualization.framework，在 Apple Silicon 上效能接近原生
- 開發者可在 VS Code 中統一開發 Android 與 iOS，降低工具切換成本
- VM 環境可版本控制與重建，確保開發環境一致性
- 與 Android 的 Docker devcontainer 開發體驗對稱

**限制：**
- 僅支援 Apple Silicon macOS 作為 Host（Tart 的硬性要求）
- 開發者需在 macOS 主機載入 VS Code（無法在 Linux/Windows 上開發 iOS）
- VM 磁碟空間需求較大（macOS + Xcode 約 30~50 GB）

---

[<< 橫切關注點](08-crosscutting-concepts.md) | [目錄](00-index.md) | [品質需求 >>](10-quality-requirements.md)
