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

package com.linecorp.apngsample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.linecorp.apng.ApngDrawable
import kotlinx.android.synthetic.main.activity_main.button_gc
import kotlinx.android.synthetic.main.activity_main.button_load_image_1
import kotlinx.android.synthetic.main.activity_main.button_load_image_1_10x
import kotlinx.android.synthetic.main.activity_main.button_load_image_1_5x
import kotlinx.android.synthetic.main.activity_main.button_load_image_2_jpeg
import kotlinx.android.synthetic.main.activity_main.button_load_image_2_normal_png
import kotlinx.android.synthetic.main.activity_main.button_start
import kotlinx.android.synthetic.main.activity_main.button_stop
import kotlinx.android.synthetic.main.activity_main.imageView
import kotlinx.android.synthetic.main.activity_main.text_status

class MainActivity : AppCompatActivity() {

    private var drawable: ApngDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_load_image_1.setOnClickListener { startLoad("test.png") }
        button_load_image_1_5x.setOnClickListener { startLoad("test.png", 500, 500) }
        button_load_image_1_10x.setOnClickListener { startLoad("test.png", 1000, 1000) }
        button_load_image_2_normal_png.setOnClickListener { startLoad("normal_png.png") }
        button_load_image_2_jpeg.setOnClickListener { startLoad("jpeg.jpg") }

        button_start.setOnClickListener { startAnimation() }
        button_stop.setOnClickListener { stopAnimation() }
        button_gc.setOnClickListener { runGc() }
    }

    @SuppressLint("SetTextI18n")
    private fun startLoad(name: String, width: Int? = null, height: Int? = null) {
        //drawable?.recycle()
        drawable = null
        imageView.setImageDrawable(null)
        val isApng = assets.open(name).buffered().use {
            ApngDrawable.isApng(it)
        }
        text_status.text = "isApng: $isApng"
        if (isApng) {
            drawable = ApngDrawable.decode(assets, name, width, height)
            drawable?.setTargetDensity(resources.displayMetrics)
            imageView.setImageDrawable(drawable)
            imageView.scaleType = ImageView.ScaleType.CENTER
        }
        Log.d("apng", "size: ${drawable?.allocationByteCount} byte")
    }

    private fun startAnimation() {
        drawable?.start()
    }

    private fun stopAnimation() {
        drawable?.stop()
    }

    private fun runGc() {
        System.gc()
    }
}
