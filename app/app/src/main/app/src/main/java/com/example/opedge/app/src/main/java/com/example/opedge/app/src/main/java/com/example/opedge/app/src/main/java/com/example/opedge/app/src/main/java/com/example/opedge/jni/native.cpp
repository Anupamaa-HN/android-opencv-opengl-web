#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <opencv2/opencv.hpp>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "EDGEPROC", __VA_ARGS__)
using namespace cv;

extern "C" {

JNIEXPORT void JNICALL
Java_com_example_opedge_NativeLib_process(JNIEnv *env, jclass clazz, jlong matAddr) {
    if (matAddr == 0) return;
    Mat *mat = reinterpret_cast<Mat *>(matAddr);
    if (mat->empty()) return;

    Mat src = *mat;
    Mat gray, edges, rgba;
    if (src.channels() == 4) cvtColor(src, gray, COLOR_RGBA2GRAY);
    else if (src.channels() == 3) cvtColor(src, gray, COLOR_BGR2GRAY);
    else gray = src;

    Canny(gray, edges, 50, 150);
    cvtColor(edges, rgba, COLOR_GRAY2RGBA);
    rgba.copyTo(*mat);
}

JNIEXPORT jlong JNICALL
Java_com_example_opedge_NativeLib_bitmapToMatAddress(JNIEnv *env, jclass clazz, jobject bitmap) {
    AndroidBitmapInfo info;
    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS) return 0;
    void *pixels = nullptr;
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) != ANDROID_BITMAP_RESULT_SUCCESS) return 0;

    Mat tmp(info.height, info.width, CV_8UC4, pixels);
    Mat *out = new Mat();
    tmp.copyTo(*out);

    AndroidBitmap_unlockPixels(env, bitmap);
    return reinterpret_cast<jlong>(out);
}

} // extern "C"
