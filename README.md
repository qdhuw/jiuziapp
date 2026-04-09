# 斑鸠鸠叫 - Android 版本

基于 Windows 版本移植的 Android 应用，点击屏幕播放斑鸠叫声。

## 功能特性

- 🎵 点击播放鸠叫（90%无损，10%全损）
- ✨ 全损时触发闪出效果（可开关）
- 📊 统计面板（显示点击次数）
- ⚙️ 设置菜单

## 项目结构

```
android-project/
├── app/
│   ├── src/main/
│   │   ├── java/com/jiuzi/jiuzijiao/
│   │   │   └── MainActivity.kt      # 主界面代码
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── drawable/             # 素材资源
│   │   │   ├── raw/                  # 音频资源
│   │   │   └── values/               # 字符串、颜色、主题
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── .github/workflows/
│   └── android.yml                   # GitHub Actions 配置
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

## GitHub Actions 自动打包

### 触发条件

| 事件 | 触发的任务 |
|------|-----------|
| Push 到 main/master 分支 | Debug APK + Release APK |
| Pull Request | Debug APK |
| 手动触发 (workflow_dispatch) | Debug APK |

### 构建产物

| 产物名称 | 说明 |
|----------|------|
| `jiuzi-debug-apk` | Debug 版本 APK |
| `jiuzi-release-apk` | Release 版本 APK |

### 下载 APK

在 GitHub 仓库的 Actions 页面，选择对应的 workflow run，然后下载构建产物。

## 本地构建

### 前提条件

- Android SDK API 34
- JDK 17

### 构建命令

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease
```

## 素材来源

素材文件位于 `app/src/main/res/` 目录下：
- `drawable/bg_jiuzi.jpg` - 背景图
- `drawable/bg_jiuzi_flash.jpg` - 闪出特效图
- `raw/sound_lossless.mp3` - 无损鸠叫音效
- `raw/sound_lossy.mp3` - 全损鸠叫音效
