#include <jni.h>
#include <string>
#include <stdio.h>
#include "src/rnnoise/rnnoise.h"

#define FRAME_SIZE 480

extern "C"
JNIEXPORT jstring JNICALL
Java_com_rnnoiseandroid_MainActivity_denoise(JNIEnv *env, jobject instance, jstring path_, jstring output_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *output = env->GetStringUTFChars(output_, 0);
    // TODO
    int first = 1;
    float x[FRAME_SIZE];
    FILE *f1, *fout;
    DenoiseState *st;
    st = rnnoise_create();

    f1 = fopen(path, "rb");
    fout = fopen(output, "wb");

    short tmp[FRAME_SIZE] = {0};

    while (1) {
        fread(tmp, sizeof(short), FRAME_SIZE, f1);
        if (feof(f1)) {
            break;
        }

        for (int i = 0; i < FRAME_SIZE; i++) {
            x[i] = tmp[i];
        }

        rnnoise_process_frame(st, x, x);
        //printf("%li processed", ftell(f1));
        for (int i = 0; i < FRAME_SIZE; i++) {
            tmp[i] = x[i];
        }

        if (!first) {
            fwrite(tmp, sizeof(short), FRAME_SIZE, fout);
        }

        first = 0;
    }
    rnnoise_destroy(st);
    fclose(f1);
    fclose(fout);

    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(output_, output);

    return env->NewStringUTF(output);
}
