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

#include "ApngImage.h"
#include "StreamSource.h"

namespace apng_drawable {

/**
 * Creates the integer value containing color information with ABGR format.
 * Android is using RGBA format internally, and uses ABGR because of endianness.
 */
inline uint32_t abgr(uint32_t a, uint32_t b, uint32_t g, uint32_t r) {
  return r | g << 8u | b << 16u | a << 24u;
}

/**
 * Returns `a * b / 255` and rounding any fractional bits.
 */
inline uint32_t div255Round(uint32_t a, uint32_t b) {
  return a * b * 0x8081u >> 23u & 0xFFu;
}

class ApngDecoder {
 public:
  static std::unique_ptr<ApngImage> decode(std::unique_ptr<StreamSource> source, int &status);
  static bool isApng(std::unique_ptr<StreamSource> source);
 private:
};
}
