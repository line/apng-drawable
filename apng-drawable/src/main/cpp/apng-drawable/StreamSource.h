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

#pragma once

#include <jni.h>
#include "png.h"

namespace apng_drawable {

class StreamSource {
 public:
  StreamSource(JNIEnv *env, jobject inputStream);
  StreamSource() = delete;
  ~StreamSource();

  int32_t checkPngSignature();
  void init(png_structp png);
  int32_t getError() { return mError; }
  static void registerJavaClass(JNIEnv *env);
  static void unregisterJavaClass(JNIEnv *env);
 private:
  JNIEnv *mEnv;
  jobject mInputStream;
  jbyteArray mBuffer;
  int32_t mError;

  static void reader(png_structp png, png_bytep data, png_size_t length);

  // Do not copy this object.
  StreamSource(const StreamSource &ref);
  StreamSource &operator=(const StreamSource &ref);
};
}
