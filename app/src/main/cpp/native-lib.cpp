#include <jni.h>
#include <string>
#include <algorithm>
#include <climits>
#include <android/log.h>

#define LOG_TAG "JNI_DEMO"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// 1) Hello World natif
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_jnidemo_MainActivity_helloFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    LOGI("Appel de helloFromJNI depuis le natif");
    return env->NewStringUTF("Hello from C++ via JNI !");
}

// 2) Factoriel avec gestion d'erreur
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_jnidemo_MainActivity_factorial(
        JNIEnv* env,
        jobject /* this */,
        jint n) {

    if (n < 0) {
        LOGE("Erreur : n negatif");
        return -1;
    }

    long long fact = 1;
    for (int i = 1; i <= n; i++) {
        fact *= i;
        if (fact > INT_MAX) {
            LOGE("Overflow detecte pour n=%d", n);
            return -2;
        }
    }

    LOGI("Factoriel de %d calcule en natif = %lld", n, fact);
    return static_cast<jint>(fact);
}

// 3) Inversion d'une chaine Java -> C++ -> Java
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_jnidemo_MainActivity_reverseString(
        JNIEnv* env,
        jobject /* this */,
        jstring javaString) {

    if (javaString == nullptr) {
        LOGE("Chaine nulle recue");
        return env->NewStringUTF("Erreur : chaine nulle");
    }

    const char* chars = env->GetStringUTFChars(javaString, nullptr);
    if (chars == nullptr) {
        LOGE("Impossible de lire la chaine Java");
        return env->NewStringUTF("Erreur JNI");
    }

    std::string s(chars);
    env->ReleaseStringUTFChars(javaString, chars);

    std::reverse(s.begin(), s.end());

    LOGI("String inversee = %s", s.c_str());
    return env->NewStringUTF(s.c_str());
}

// 4) Somme d'un tableau int[]
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_jnidemo_MainActivity_sumArray(
        JNIEnv* env,
        jobject /* this */,
        jintArray array) {

    if (array == nullptr) {
        LOGE("Tableau nul");
        return -1;
    }

    jsize len = env->GetArrayLength(array);
    jint* elements = env->GetIntArrayElements(array, nullptr);

    if (elements == nullptr) {
        LOGE("Impossible d'acceder aux elements du tableau");
        return -2;
    }

    long long sum = 0;
    for (jsize i = 0; i < len; i++) {
        sum += elements[i];
    }

    env->ReleaseIntArrayElements(array, elements, 0);

    if (sum > INT_MAX) {
        LOGE("Overflow sur la somme");
        return -3;
    }

    LOGI("Somme du tableau = %lld", sum);
    return static_cast<jint>(sum);
}

// Extension : Multiplication matricielle 2x2
extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_jnidemo_MainActivity_multiplyMatrices(
        JNIEnv* env,
        jobject /* this */,
        jintArray matrixA,
        jintArray matrixB) {

    if (matrixA == nullptr || matrixB == nullptr) return nullptr;

    jsize lenA = env->GetArrayLength(matrixA);
    jsize lenB = env->GetArrayLength(matrixB);

    if (lenA != 4 || lenB != 4) return nullptr;

    jint* a = env->GetIntArrayElements(matrixA, nullptr);
    jint* b = env->GetIntArrayElements(matrixB, nullptr);

    jintArray resultObj = env->NewIntArray(4);
    jint res[4];

    // Calcul Multiplication 2x2
    // [0 1]   [0 1]
    // [2 3] x [2 3]
    res[0] = a[0] * b[0] + a[1] * b[2];
    res[1] = a[0] * b[1] + a[1] * b[3];
    res[2] = a[2] * b[0] + a[3] * b[2];
    res[3] = a[2] * b[1] + a[3] * b[3];

    env->SetIntArrayRegion(resultObj, 0, 4, res);

    env->ReleaseIntArrayElements(matrixA, a, JNI_ABORT);
    env->ReleaseIntArrayElements(matrixB, b, JNI_ABORT);

    LOGI("Multiplication matricielle effectuee");
    return resultObj;
}