//
// Copyright 2018 LINE Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

#include "StreamSource.h"
#include "Error.h"
#include "ApngImage.h"

namespace apng_drawable {

static jclass gInputStream_class;
static jmethodID gInputStream_readMethodID;

void StreamSource::registerJavaClass(JNIEnv *env) {
  jclass inputStream_class = env->FindClass("java/io/InputStream");
  gInputStream_class = reinterpret_cast<jclass>(env->NewGlobalRef(inputStream_class));
  env->DeleteLocalRef(inputStream_class);
  gInputStream_readMethodID = env->GetMethodID(gInputStream_class, "read", "([BII)I");
}

void StreamSource::unregisterJavaClass(JNIEnv *env) {
  env->DeleteGlobalRef(gInputStream_class);
  gInputStream_readMethodID = nullptr;
}

StreamSource::StreamSource(JNIEnv *env, jobject inputStream)
    : mEnv(env), mBuffer(nullptr), mError(SUCCESS) {
  mInputStream = env->NewGlobalRef(inputStream);
}

StreamSource::~StreamSource() {
  mEnv->DeleteGlobalRef(mInputStream);
  mEnv->DeleteGlobalRef(mBuffer);
}

int32_t StreamSource::checkPngSignature() {
  // Check signature
  jbyteArray sig_bytearray = mEnv->NewByteArray(PNG_SIG_SIZE);
  jint read =
      mEnv->CallIntMethod(mInputStream, gInputStream_readMethodID, sig_bytearray, 0, PNG_SIG_SIZE);
  if (mEnv->ExceptionOccurred()) {
    mEnv->ExceptionClear();
    mEnv->DeleteLocalRef(sig_bytearray);
    return ERR_STREAM_READ_FAIL;
  } else if (read < 0) {
    mEnv->DeleteLocalRef(sig_bytearray);
    return ERR_UNEXPECTED_EOF;
  }
  jbyte *sig = mEnv->GetByteArrayElements(sig_bytearray, nullptr);
  if (png_sig_cmp(reinterpret_cast<png_const_bytep>(sig), 0, PNG_SIG_SIZE) != 0) {
    mEnv->ReleaseByteArrayElements(sig_bytearray, sig, JNI_ABORT);
    mEnv->DeleteLocalRef(sig_bytearray);
    return ERR_INVALID_FILE_FORMAT;
  }
  mEnv->ReleaseByteArrayElements(sig_bytearray, sig, JNI_ABORT);
  mEnv->DeleteLocalRef(sig_bytearray);
  return SUCCESS;
}

void StreamSource::init(png_structp png_ptr) {
  png_set_read_fn(png_ptr, this, reader);
}

void StreamSource::reader(png_structp png, png_bytep data, png_size_t length) {
  auto *source = static_cast<StreamSource *>(png_get_io_ptr(png));
  JNIEnv *env = source->mEnv;

  if (!source->mBuffer || env->GetArrayLength(source->mBuffer) < length) {
    env->DeleteGlobalRef(source->mBuffer);
    source->mBuffer = reinterpret_cast<jbyteArray>(
        env->NewGlobalRef(env->NewByteArray(static_cast<jsize>(length)))
    );
  }
  uint32_t offset = 0;
  do {
    jint readBytes = env->CallIntMethod(
        source->mInputStream,
        gInputStream_readMethodID,
        source->mBuffer,
        offset,
        length
    );
    if (env->ExceptionOccurred()) {
      env->ExceptionClear();
      source->mError = ERR_STREAM_READ_FAIL;
      png_error(png, "");
    } else if (readBytes < 0) {
      source->mError = ERR_UNEXPECTED_EOF;
      png_error(png, "");
    }
    offset += readBytes;
    length -= readBytes;
  } while (length > 0);

  env->GetByteArrayRegion(source->mBuffer, 0, offset, reinterpret_cast<jbyte *>(data));
}
}
