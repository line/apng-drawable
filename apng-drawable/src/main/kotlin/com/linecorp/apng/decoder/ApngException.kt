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

package com.linecorp.apng.decoder

/**
 * Thrown when an error occurs when loads APNG.
 * The detailed cause is described in [errorCode].
 */
class ApngException internal constructor(
    /**
     *  An error code that is the reason for this exception.
     */
    val errorCode: ErrorCode,
    throwable: Throwable? = null
) : Exception(throwable) {

    internal constructor(
        throwable: Throwable
    ) : this(ErrorCode.ERR_WITH_CHILD_EXCEPTION, throwable)

    override val message: String?
        get() = when (errorCode) {
            ErrorCode.ERR_STREAM_READ_FAIL -> "Can't read the stream."
            ErrorCode.ERR_UNEXPECTED_EOF -> "Unexpected end of file."
            ErrorCode.ERR_INVALID_FILE_FORMAT -> "Invalid file format."
            ErrorCode.ERR_NOT_EXIST_IMAGE -> "Not exist native image."
            ErrorCode.ERR_FRAME_INDEX_OUT_OF_RANGE -> "Frame index is out of range."
            ErrorCode.ERR_OUT_OF_MEMORY -> "Out of memory"
            ErrorCode.ERR_BITMAP_OPERATION -> "Error in the native bitmap operation."
            ErrorCode.ERR_UNSUPPORTED_TYPE -> "Unsupported image type."
            ErrorCode.ERR_WITH_CHILD_EXCEPTION -> "Failed with sub exception : " + cause?.message
        }

    // Follow constants is related with defined error code in "Error.h"
    enum class ErrorCode(val errorCode: Int) {
        /**
         * When an error occurs in [java.io.InputStream.read].
         */
        ERR_STREAM_READ_FAIL(-100),

        /**
         * When EOF came before reading the required size in [java.io.InputStream.read].
         */
        ERR_UNEXPECTED_EOF(-101),

        /**
         * When an image was not in APNG format.
         */
        ERR_INVALID_FILE_FORMAT(-102),

        /**
         * When using an image that has not yet been decoded or deleted.
         */
        ERR_NOT_EXIST_IMAGE(-103),

        /**
         * When using a frame outside the range of this image.
         */
        ERR_FRAME_INDEX_OUT_OF_RANGE(-104),

        /**
         * When memory can not be allocated.
         */
        ERR_OUT_OF_MEMORY(-105),

        /**
         * When an error occurs in the operation to the [android.graphics.Bitmap].
         */
        ERR_BITMAP_OPERATION(-106),

        /**
         * When an image is not supported APNG image.
         * Example: tries to load non-4 channel image.(Currently, supported 4 channel image only.)
         */
        ERR_UNSUPPORTED_TYPE(-107),

        /**
         * When there is an inner exception.
         */
        ERR_WITH_CHILD_EXCEPTION(-200);

        companion object {
            internal fun fromErrorCode(errorCode: Int): ErrorCode =
                ErrorCode.values().first { it.errorCode == errorCode }
        }
    }
}
