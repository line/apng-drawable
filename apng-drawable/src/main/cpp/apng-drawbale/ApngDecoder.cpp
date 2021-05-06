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

#include "ApngDecoder.h"
#include <memory>
#include <cmath>
#include "zlib.h"
#include "png.h"
#include "Error.h"
#include "StreamSource.h"
#include "Log.h"

namespace apng_drawable {

const uint8_t ALPHA_TRANSPARENT = 0U;
const uint8_t ALPHA_OPAQUE = 0xFFU;
const size_t CHANNEL_4_BYTE_SIZE = sizeof(uint8_t) * 4;

inline void saveFrame(uint32_t *destination,
                      uint32_t **const source,
                      uint32_t const width,
                      uint32_t const height) {
  if (!destination) {
    return;
  }
  uint_fast8_t alpha;
  uint32_t src_color;
  // default
  for (uint32_t j = 0; j < height; ++j, destination += width) {
    memcpy(destination, source[j], width * CHANNEL_4_BYTE_SIZE);
    for (uint32_t i = 0; i < width; ++i) {
      // pre multiply color
      src_color = destination[i];
      alpha = static_cast<uint_fast8_t>((src_color >> 24U) & 0xFFU);
      if (alpha == ALPHA_TRANSPARENT) {
        // transparent
        destination[i] = 0;
        continue;
      }
      if (alpha == ALPHA_OPAQUE) {
        // opaque
        continue;
      }
      // translucent
      destination[i] = abgr(
          alpha,
          div255Round(src_color >> 16U & 0xFFU, alpha),
          div255Round(src_color >> 8U & 0xFFU, alpha),
          div255Round(src_color & 0xFFU, alpha));
    }
  }
}

inline void blendOver(
    uint8_t **destination,
    uint8_t **const source,
    const png_uint_32 x_offset,
    const png_uint_32 y_offset,
    const png_uint_32 width,
    const png_uint_32 height
) {
  for (uint32_t j = 0; j < height; ++j) {
    uint8_t *sp = source[j];
    uint8_t *dp = destination[j + y_offset] + x_offset * CHANNEL_4_BYTE_SIZE;
    uint8_t sourceAlpha;
    for (uint32_t i = 0; i < width; ++i, sp += CHANNEL_4_BYTE_SIZE, dp += CHANNEL_4_BYTE_SIZE) {
      sourceAlpha = sp[3];
      if (sourceAlpha == ALPHA_OPAQUE) {
        memcpy(dp, sp, CHANNEL_4_BYTE_SIZE);
      } else if (sourceAlpha != ALPHA_TRANSPARENT) {
        if (dp[3] != ALPHA_TRANSPARENT) {
          int32_t u = sourceAlpha * ALPHA_OPAQUE;
          int32_t v = (ALPHA_OPAQUE - sourceAlpha) * dp[3];
          int32_t al =
              ALPHA_OPAQUE * ALPHA_OPAQUE - (ALPHA_OPAQUE - sourceAlpha) * (ALPHA_OPAQUE - dp[3]);
          dp[0] = static_cast<uint8_t>((sp[0] * u + dp[0] * v) / al);
          dp[1] = static_cast<uint8_t>((sp[1] * u + dp[1] * v) / al);
          dp[2] = static_cast<uint8_t>((sp[2] * u + dp[2] * v) / al);
          dp[3] = static_cast<uint8_t>(al / ALPHA_OPAQUE);
        } else {
          memcpy(dp, sp, CHANNEL_4_BYTE_SIZE);
        }
      }
    }
  }
}

inline void blendSource(
    uint8_t **destination,
    uint8_t **const source,
    png_uint_32 x_offset,
    png_uint_32 y_offset,
    png_uint_32 width,
    png_uint_32 height
) {
  for (uint32_t j = 0; j < height; j++) {
    memcpy(destination[j + y_offset] + x_offset * CHANNEL_4_BYTE_SIZE,
           source[j],
           width * CHANNEL_4_BYTE_SIZE);
  }
}

std::unique_ptr<ApngImage> ApngDecoder::decode(
    std::unique_ptr<StreamSource> source,
    int32_t &result
) {
  // Check signature
  LOGV(" | check signature");
  int32_t format_check_result = source->checkPngSignature();
  if (format_check_result < SUCCESS) {
    result = format_check_result;
    return nullptr;
  }

  // Create structure
  LOGV(" | create structure");
  png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, nullptr, nullptr, nullptr);
  png_infop info_ptr = png_create_info_struct(png_ptr);
  if (!png_ptr || !info_ptr) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_OUT_OF_MEMORY;
    return nullptr;
  }

  // Point to handle error (Read header and acTL)
  if (setjmp(png_jmpbuf(png_ptr)) != 0) { // NOLINT(cert-err52-cpp)
    result = source->getError();
    if (!result) {
      result = ERR_INVALID_FILE_FORMAT;
    }
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    return nullptr;
  }

  // Read header
  LOGV(" | read header");
  source->init(png_ptr);
  png_set_sig_bytes(png_ptr, PNG_SIG_SIZE);
  png_read_info(png_ptr, info_ptr);

  png_set_expand(png_ptr);
  png_set_strip_16(png_ptr);
  png_set_gray_to_rgb(png_ptr);
  png_set_add_alpha(png_ptr, 0xff, PNG_FILLER_AFTER);
  png_set_interlace_handling(png_ptr);
  png_read_update_info(png_ptr, info_ptr);

  auto width = static_cast<uint32_t>(png_get_image_width(png_ptr, info_ptr));
  auto height = static_cast<uint32_t>(png_get_image_height(png_ptr, info_ptr));
  uint channels = png_get_channels(png_ptr, info_ptr);
  size_t row_bytes = png_get_rowbytes(png_ptr, info_ptr);

  // check decode bound
  LOGV(" | check decode bound (w: %d, h: %d)", width, height);
  if (width <= 0 || height <= 0) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_INVALID_FILE_FORMAT;
    return nullptr;
  }
  // check channel (supported only 4 channel apng)
  if (channels != 4) {
    result = ERR_UNSUPPORTED_TYPE;
    return nullptr;
  }

  // Read acTL
  LOGV(" | read acTL");
  png_uint_32 frames = 1;
  png_uint_32 plays = 0;
  bool has_acTL = png_get_acTL(png_ptr, info_ptr, &frames, &plays) != 0;
  if (!has_acTL) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_INVALID_FILE_FORMAT;
    return nullptr;
  }

  // Allocate buffers
  LOGV(" | allocate buffers");
  // Check unsigned integer wrapping
  if (height > SIZE_MAX / row_bytes) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_INVALID_FILE_FORMAT;
    return nullptr;
  }
  size_t size = height * row_bytes;
  std::unique_ptr<uint8_t[]> p_frame(new uint8_t[size]());
  std::unique_ptr<uint8_t[]> p_buffer(new uint8_t[size]());
  std::unique_ptr<uint8_t[]> p_previous_frame(new uint8_t[size]());
  // Check unsigned integer wrapping
  if (height > SIZE_MAX / sizeof(png_bytep)) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_INVALID_FILE_FORMAT;
    return nullptr;
  }
  size_t row_ptr_array_size = height * sizeof(png_bytep);
  std::unique_ptr<png_bytep[]> rows_frame(new png_bytep[row_ptr_array_size]);
  std::unique_ptr<png_bytep[]> rows_buffer(new png_bytep[row_ptr_array_size]);
  if (!p_frame || !p_buffer || !p_previous_frame || !rows_frame || !rows_buffer) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_OUT_OF_MEMORY;
    return nullptr;
  }
  for (uint32_t j = 0; j < height; j++) {
    rows_frame[j] = p_frame.get() + j * row_bytes;
  }
  for (uint32_t j = 0; j < height; j++) {
    rows_buffer[j] = p_buffer.get() + j * row_bytes;
  }

  std::unique_ptr<ApngImage> png;
  try {
    png = std::make_unique<ApngImage>(
        width,
        height,
        static_cast<uint32_t>(frames),
        static_cast<uint32_t>(plays)
    );
  } catch (const std::bad_alloc &) {
    LOGV(" | failed to allocate ApngImage due to std::bad_alloc");
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_OUT_OF_MEMORY;
    return nullptr;
  }

  if (!png) {
    LOGV(" | failed to allocate ApngImage");
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    result = ERR_OUT_OF_MEMORY;
    return nullptr;
  }

  // Point to handle error (everything until done decoding)
  if (setjmp(png_jmpbuf(png_ptr)) != 0) { // NOLINT(cert-err52-cpp)
    // set error code
    result = source->getError();
    if (!result) {
      result = ERR_INVALID_FILE_FORMAT;
    }

    // release all
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    return nullptr;
  }

  // Read frames
  LOGV(" | read frames");
  png_uint_32 x_offset = 0;
  png_uint_32 y_offset = 0;
  png_uint_32 frame_width = width;
  png_uint_32 frame_height = height;
  uint16_t delay_num = 1;
  uint16_t delay_den = 100;
  uint8_t dispose_op = 0;
  uint8_t blend_op = 0;
  uint32_t first = (png_get_first_frame_is_hidden(png_ptr, info_ptr) != 0) ? 1 : 0;
  for (uint32_t i = 0; i < frames; ++i) {
    // Read fcTL
    LOGV(" | read fcTL (%d)", i);
    png_read_frame_head(png_ptr, info_ptr);
    png_get_next_frame_fcTL(png_ptr,
                            info_ptr,
                            &frame_width,
                            &frame_height,
                            &x_offset,
                            &y_offset,
                            &delay_num,
                            &delay_den,
                            &dispose_op,
                            &blend_op);
    auto duration =
        static_cast<size_t>(std::lround(static_cast<float>(delay_num) / delay_den * 1000.F));
    std::unique_ptr<ApngFrame> frame(new ApngFrame(size, duration));
    if (i == first) {
      blend_op = PNG_BLEND_OP_SOURCE;
      if (dispose_op == PNG_DISPOSE_OP_PREVIOUS) {
        dispose_op = PNG_DISPOSE_OP_BACKGROUND;
      }
    }

    // Read fdAT or IDAT
    png_read_image(png_ptr, rows_buffer.get());

    // Process dispose operation
    if (dispose_op == PNG_DISPOSE_OP_PREVIOUS) {
      memcpy(p_previous_frame.get(), p_frame.get(), size);
    }

    // Process blend operation
    if (blend_op == PNG_BLEND_OP_OVER) {
      blendOver(rows_frame.get(), rows_buffer.get(), x_offset, y_offset, frame_width, frame_height);
    } else { // PNG_BLEND_OP_SOURCE
      blendSource(rows_frame.get(),
                  rows_buffer.get(),
                  x_offset,
                  y_offset,
                  frame_width,
                  frame_height);
    }

    // Save frame
    saveFrame(
        frame->getRawPixels(), reinterpret_cast<uint32_t **const>(rows_frame.get()),
        width,
        height);

    // Process dispose operation after decode frame
    if (dispose_op == PNG_DISPOSE_OP_PREVIOUS) {
      memcpy(p_frame.get(), p_previous_frame.get(), size);
    } else if (dispose_op == PNG_DISPOSE_OP_BACKGROUND) {
      for (uint32_t j = 0; j < frame_height; j++) {
        memset(rows_frame[j + y_offset] + x_offset * CHANNEL_4_BYTE_SIZE,
               0,
               frame_width * CHANNEL_4_BYTE_SIZE);
      }
    }
    png->setFrame(i, std::move(frame));
  }

  // Finish read
  LOGV(" | finish decode");
  png_read_end(png_ptr, info_ptr);
  png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
  result = SUCCESS;
  return std::move(png);
}

bool ApngDecoder::isApng(std::unique_ptr<StreamSource> source) {
  // Checks PNG signature
  int result = source->checkPngSignature();
  if (result != SUCCESS) {
    return false;
  }

  // Checks APNG acTL chunk
  png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, nullptr, nullptr, nullptr);
  png_infop info_ptr = png_create_info_struct(png_ptr);
  if (!png_ptr || !info_ptr) {
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    return false;
  }

  // Point to handle error (Read header and acTL)
  if (setjmp(png_jmpbuf(png_ptr)) != 0) { // NOLINT(cert-err52-cpp)
    png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
    return false;
  }

  // Read header
  source->init(png_ptr);
  png_set_sig_bytes(png_ptr, 8);
  png_read_info(png_ptr, info_ptr);

  // Read acTL
  png_uint_32 frames = 0;
  png_uint_32 plays = 0;
  bool has_acTL = png_get_acTL(png_ptr, info_ptr, &frames, &plays) != 0;
  png_destroy_read_struct(&png_ptr, &info_ptr, nullptr);
  return has_acTL;
}
}
