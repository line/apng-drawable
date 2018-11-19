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
#include "ApngFrame.h"

const size_t PNG_SIG_SIZE = 8;

namespace apng_drawable {

class ApngImage {
 public:
  ApngImage(uint32_t width, uint32_t height, uint32_t frameCount, uint32_t loopCount);
  ApngImage() = delete;
  uint32_t getWidth() const { return mWidth; }
  uint32_t getHeight() const { return mHeight; }
  uint32_t getTotalDuration() const;
  uint32_t getRepeatCount() const { return mLoopCount; }
  uint32_t getFrameCount() const { return mFrameCount; }
  uint32_t getFrameByteCount() const { return sizeof(uint32_t) * mWidth * mHeight; }
  uint64_t getAllFrameByteCount() const { return getFrameByteCount() * mFrameCount; }
  std::shared_ptr<ApngFrame> getFrame(uint32_t index) const;
  void setFrame(uint32_t index, std::unique_ptr<ApngFrame> frame);

 private:
  std::unique_ptr<std::shared_ptr<ApngFrame>[]> mFrames;
  uint32_t mWidth;
  uint32_t mHeight;
  uint32_t mFrameCount;
  uint32_t mLoopCount;

  // Do not copy this object.
  ApngImage(const ApngImage &ref);
  ApngImage &operator=(const ApngImage &ref);
};
}
