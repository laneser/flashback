# Flashback — AI 開發指引

## 專案概述

Flashback 是一套 Android / iOS 飆車聲音偵測與閃光嚇阻系統。利用手機麥克風持續監聽環境音，透過 AI 辨識飆車聲音特徵後，自動觸發閃光燈模擬測速照相，達到嚇阻效果。支援多機 WebSocket 聯動閃光。iOS 版本為計畫中。

**目前狀態：** 初始 scaffold 階段（僅有 "Hello Flashback!" 畫面）。

## 技術棧

| 項目 | 版本 / 規格 |
|------|-------------|
| 語言 | Kotlin 2.0.21 |
| UI 框架 | Jetpack Compose (BOM 2024.12.01) |
| 建置工具 | Gradle 8.11.1 (Kotlin DSL) |
| Android Gradle Plugin | 8.7.3 |
| Min SDK | 24 (Android 7.0) |
| Target / Compile SDK | 35 |
| JDK | 17 |
| 版本管理 | Gradle Version Catalog (`libs.versions.toml`) |

## 專案結構

```
flashback/
├── app/
│   ├── build.gradle.kts          # App 模組建置設定
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/flashback/app/
│           ├── MainActivity.kt   # 目前唯一 Activity
│           └── ui/theme/         # Compose 主題（Color, Theme, Type）
├── gradle/
│   └── libs.versions.toml        # 版本目錄（所有依賴版本集中管理）
├── build.gradle.kts              # 根層建置設定
├── settings.gradle.kts           # 專案設定（模組宣告）
├── .devcontainer/                # Docker 開發容器
│   ├── Dockerfile
│   └── devcontainer.json
└── docs/
    ├── proposal.md               # 專案提案文件
    └── ARC42/                    # 系統架構文件（ARC42 模板）
```

## 建置指令

```bash
# 建置 Debug APK
./gradlew assembleDebug

# 執行 Lint 檢查
./gradlew lint

# 清除建置產物
./gradlew clean

# 執行單元測試（尚未建立）
./gradlew test

# 執行 instrumented 測試（尚未建立）
./gradlew connectedAndroidTest
```

## 編碼慣例

- **Kotlin 風格：** 遵循 [Kotlin 官方編碼風格](https://kotlinlang.org/docs/coding-conventions.html)
- **Compose UI：** 使用 Material 3 元件，Composable 函式以 PascalCase 命名
- **Package 結構：** `com.flashback.app` 為根 package
- **依賴管理：** 所有依賴必須透過 `gradle/libs.versions.toml` 版本目錄管理，不在 `build.gradle.kts` 中寫死版本號
- **字串資源：** 使用者可見文字應放在 `strings.xml`，不在程式碼中 hardcode
- **繁體中文：** 文件與註解使用繁體中文，程式碼（變數名、函式名）使用英文

## 計畫中的依賴（尚未加入）

| 依賴 | 用途 | 階段 |
|------|------|------|
| TarsosDSP | 音訊 FFT 頻譜分析 | Phase 1 |
| TensorFlow Lite | YAMNet 聲音分類模型 | Phase 2 |
| Ktor (WebSocket) | 多機聯動通訊 | Phase 3 |
| CameraX | 相機閃光燈控制與拍照 | Phase 1 |
| DataStore | 使用者設定持久化 | Phase 4 |

### iOS 計畫中的技術棧（Phase 6）

| 項目 | 技術 |
|------|------|
| 語言 | Swift |
| UI | SwiftUI |
| 音訊 | AVAudioEngine |
| ML | Core ML / TFLite |
| 閃光燈 | AVCaptureDevice |
| 網路 | URLSessionWebSocketTask 或 Starscream |
| 開發環境 | Tart macOS VM + VS Code SSH Remote |

## 架構筆記

- **架構模式：** MVVM（Model-View-ViewModel）搭配 Compose
- **音訊管線：** AudioRecord → RMS 計算 → FFT 分析 → YAMNet 推論 → 觸發決策
- **觸發邏輯：** AND 條件（音量 > 閾值 AND YAMNet 信心度 > 70% AND 持續 > 0.3s AND 在設定時段內）
- **多機通訊：** 主裝置作為 Ktor WebSocket Server，從裝置為 Client，觸發事件透過 WebSocket 廣播
- **離線運作：** 所有 ML 推論在裝置端完成，不需網路連線（多機聯動除外）

## 測試策略

- **單元測試：** 音訊分析邏輯、觸發條件判斷（JUnit 5 + MockK）
- **UI 測試：** Compose UI 測試（`androidx.compose.ui:ui-test-junit4`）
- **Instrumented 測試：** 音訊錄製、相機閃光燈整合測試
- **手動測試：** 實際環境聲音偵測效果驗證

## 開發環境

專案提供 Docker devcontainer，包含：
- Ubuntu 22.04
- OpenJDK 17
- Gradle 8.11.1
- Android SDK（platform-tools, android-35, build-tools 35.0.0）

VS Code 擴充套件：`fwcd.kotlin`, `vscjava.vscode-gradle`

### iOS 開發環境（計畫中）

iOS 開發使用 **Tart**（Apple Silicon 原生 macOS VM）搭配 VS Code + SSH Remote，達到與 Android devcontainer 相同的遠端開發體驗：

- macOS 主機安裝 Tart，建立 macOS VM
- VM 內安裝 Xcode + Swift toolchain
- 從 macOS 主機以 VS Code SSH Remote 連線至 VM 開發
- 差別：需在 macOS 載入 VS Code（Tart 僅支援 macOS / Linux host with Apple Silicon）

## 重要注意事項

- 本專案目前僅有 scaffold，大部分功能為 **計畫中** 狀態
- 修改依賴時務必更新 `libs.versions.toml`，勿在 `build.gradle.kts` 直接寫版本
- 音訊處理需注意 Android 權限（`RECORD_AUDIO`）與背景執行限制
- 閃光燈控制需要 `CAMERA` 權限
- 詳細系統架構參見 `docs/ARC42/` 目錄
- 完整專案提案參見 `docs/proposal.md`
