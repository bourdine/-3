#include <jni.h>
#include <android/log.h>
#include <thread>
#include <atomic>
#include <cstring>
#include <unistd.h>

#define LOG_TAG "NativeMiner"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Объявления функций из libxmrig.so
extern "C" {
    int xmrig_start(const char* pool, const char* wallet, int threads);
    void xmrig_stop();
    double xmrig_hashrate();
    long xmrig_accepted();
    long xmrig_rejected();
}

static std::atomic<bool> isRunning{false};
static double currentHashrate = 0.0;
static long acceptedShares = 0;
static long rejectedShares = 0;

// Поток мониторинга
void* monitorThread(void* arg) {
    LOGD("Monitor thread started");
    while (isRunning) {
        currentHashrate = xmrig_hashrate();
        acceptedShares = xmrig_accepted();
        rejectedShares = xmrig_rejected();
        LOGD("Stats - HR: %.2f, A: %ld, R: %ld", currentHashrate, acceptedShares, rejectedShares);
        sleep(2);
    }
    LOGD("Monitor thread stopped");
    return nullptr;
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_lottttto_miner_utils_NativeMinerLib_startMining(
        JNIEnv* env, jobject thiz,
        jstring poolUrl, jstring walletAddress,
        jstring workerName, jstring password,
        jstring algo, jint threads) {

    if (isRunning) {
        LOGD("Miner already running");
        return JNI_TRUE;
    }

    const char* pool = env->GetStringUTFChars(poolUrl, nullptr);
    const char* wallet = env->GetStringUTFChars(walletAddress, nullptr);
    const char* worker = env->GetStringUTFChars(workerName, nullptr);
    const char* pass = env->GetStringUTFChars(password, nullptr);
    const char* algorithm = env->GetStringUTFChars(algo, nullptr);

    LOGD("Starting XMRig with pool=%s, wallet=%s, worker=%s, algo=%s, threads=%d",
         pool, wallet, worker, algorithm, threads);

    int result = xmrig_start(pool, wallet, threads);

    if (result == 0) {
        isRunning = true;
        pthread_t thread;
        pthread_create(&thread, nullptr, monitorThread, nullptr);
        pthread_detach(thread);
        LOGD("XMRig started successfully");
    } else {
        LOGE("Failed to start XMRig, error code: %d", result);
    }

    env->ReleaseStringUTFChars(poolUrl, pool);
    env->ReleaseStringUTFChars(walletAddress, wallet);
    env->ReleaseStringUTFChars(workerName, worker);
    env->ReleaseStringUTFChars(password, pass);
    env->ReleaseStringUTFChars(algo, algorithm);

    return result == 0 ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_lottttto_miner_utils_NativeMinerLib_stopMining(JNIEnv* env, jobject thiz) {
    if (!isRunning) {
        LOGD("Miner not running");
        return JNI_TRUE;
    }

    LOGD("Stopping XMRig");
    isRunning = false;
    xmrig_stop();
    LOGD("XMRig stopped");
    return JNI_TRUE;
}

JNIEXPORT jdouble JNICALL
Java_com_lottttto_miner_utils_NativeMinerLib_getHashrate(JNIEnv* env, jobject thiz) {
    return currentHashrate;
}

JNIEXPORT jlong JNICALL
Java_com_lottttto_miner_utils_NativeMinerLib_getAcceptedShares(JNIEnv* env, jobject thiz) {
    return acceptedShares;
}

JNIEXPORT jlong JNICALL
Java_com_lottttto_miner_utils_NativeMinerLib_getRejectedShares(JNIEnv* env, jobject thiz) {
    return rejectedShares;
}

}
