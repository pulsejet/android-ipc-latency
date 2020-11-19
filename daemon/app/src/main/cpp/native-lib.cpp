#include <jni.h>
#include <string>
#include <chrono>

extern "C"
JNIEXPORT jlong JNICALL
Java_com_lrptest_daemon_MainActivity_getNativeTime(JNIEnv *env, jobject thiz) {
    auto now = std::chrono::system_clock::now();
    auto now_us = std::chrono::time_point_cast<std::chrono::microseconds>(now);
    return now_us.time_since_epoch().count();
}