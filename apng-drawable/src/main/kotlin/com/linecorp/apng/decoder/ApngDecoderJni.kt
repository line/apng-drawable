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

import android.graphics.Bitmap
import java.io.InputStream

internal object ApngDecoderJni {

    init {
        System.loadLibrary("apng-drawable")
    }

    @JvmStatic
    external fun decode(inputStream: InputStream, result: Apng.DecodeResult): Int

    @JvmStatic
    external fun isApng(inputStream: InputStream): Boolean

    @JvmStatic
    external fun recycle(id: Int): Int

    @JvmStatic
    external fun draw(id: Int, index: Int, bitmap: Bitmap)

    @JvmStatic
    external fun copy(id: Int, result: Apng.DecodeResult): Int
}
