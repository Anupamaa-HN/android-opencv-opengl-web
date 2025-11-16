# OpEdge — Android OpenCV (C++) + OpenGL ES + Web Viewer

Minimal assessment project for Android (Camera2) + OpenCV C++ (JNI) + OpenGL ES renderer, plus a tiny TypeScript web viewer.

## Contents
- `app/` — Android app source (Kotlin)
- `jni/` — native C++ OpenCV processing (Canny edge)
- `CMakeLists.txt` — native build config (root)
- `web/` — TypeScript web viewer (sample processed frame)

## How to build (evaluator / reviewer)
1. Install Android SDK & NDK (tested with NDK 25.1).
2. Download OpenCV Android SDK (v4.x) from OpenCV releases.
   - Place SDK at repo root as `opencv-android/` so `CMakeLists.txt` can find `OpenCV_DIR`.
3. From project root:
