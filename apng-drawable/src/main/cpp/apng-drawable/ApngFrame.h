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

#include <memory>

namespace apng_drawable {

class ApngFrame {
 public:
  ApngFrame(size_t pixelCount, size_t duration);
  ApngFrame() = delete;

  uint32_t *getRawPixels() const { return mPixels.get(); }
  size_t getDuration() const { return mDuration; }
 private:
  std::unique_ptr<uint32_t[]> mPixels;
  size_t mDuration;

  // Do not copy this object.
  ApngFrame(const ApngFrame &ref);
  ApngFrame &operator=(const ApngFrame &ref);
};
}
