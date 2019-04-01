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
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.linecorp.apng.ApngDrawable
import com.linecorp.apng.RepeatAnimationCallback
import kotlinx.android.synthetic.main.activity_main.button_copy
import kotlinx.android.synthetic.main.activity_main.button_gc
import kotlinx.android.synthetic.main.activity_main.button_load_image_1
import kotlinx.android.synthetic.main.activity_main.button_load_image_1_10x
import kotlinx.android.synthetic.main.activity_main.button_load_image_1_5x
import kotlinx.android.synthetic.main.activity_main.button_load_image_2_jpeg
import kotlinx.android.synthetic.main.activity_main.button_load_image_2_normal_png
import kotlinx.android.synthetic.main.activity_main.button_mutate
import kotlinx.android.synthetic.main.activity_main.button_seek_end
import kotlinx.android.synthetic.main.activity_main.button_seek_start
import kotlinx.android.synthetic.main.activity_main.button_start
import kotlinx.android.synthetic.main.activity_main.button_stop
import kotlinx.android.synthetic.main.activity_main.imageView
import kotlinx.android.synthetic.main.activity_main.text_callback
import kotlinx.android.synthetic.main.activity_main.text_status

class MainActivity : AppCompatActivity() {

    private var drawable: ApngDrawable? = null

    @SuppressLint("SetTextI18n")
    private val animationCallback = object : AnimationCallbacks() {
        override fun onAnimationStart(drawable: Drawable?) {
            Log.d("apng", "Animation start")
            text_callback.text = "Animation started"
        }
        override fun onRepeat(drawable: ApngDrawable, nextLoop: Int) {
            val loopCount = drawable.loopCount
            Log.d("apng", "Animation repeat loopCount: $loopCount, nextLoop: $nextLoop")
            text_callback.text = "Animation repeat " +
                    "loopCount: $loopCount, " +
                    "nextLoop: $nextLoop"
        }
        override fun onAnimationEnd(drawable: Drawable?) {
            Log.d("apng", "Animation end")
            text_callback.text = "Animation ended"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_load_image_1.setOnClickListener { startLoad("test.png") }
        button_load_image_1_5x.setOnClickListener { startLoad("test.png", 500, 500) }
        button_load_image_1_10x.setOnClickListener { startLoad("test.png", 1000, 1000) }
        button_load_image_2_normal_png.setOnClickListener { startLoad("normal_png.png") }
        button_load_image_2_jpeg.setOnClickListener { startLoad("jpeg.jpg") }
        button_mutate.setOnClickListener { mutate() }
        button_copy.setOnClickListener { duplicate() }

        button_start.setOnClickListener { startAnimation() }
        button_stop.setOnClickListener { stopAnimation() }
        button_gc.setOnClickListener { runGc() }
        button_seek_start.setOnClickListener { seekTo(0L) }
        button_seek_end.setOnClickListener { seekTo(10000000L) }
    }

    @SuppressLint("SetTextI18n")
    private fun startLoad(name: String, width: Int? = null, height: Int? = null) {
        //drawable?.recycle()
        drawable?.clearAnimationCallbacks()
        drawable = null
        imageView.setImageDrawable(null)
        val isApng = assets.open(name).buffered().use {
            ApngDrawable.isApng(it)
        }
        text_status.text = "isApng: $isApng"
        if (isApng) {
            drawable = ApngDrawable.decode(assets, name, width, height)
            drawable?.loopCount = 5
            drawable?.setTargetDensity(resources.displayMetrics)
            drawable?.registerAnimationCallback(animationCallback)
            drawable?.registerRepeatAnimationCallback(animationCallback)
            imageView.setImageDrawable(drawable)
            imageView.scaleType = ImageView.ScaleType.CENTER
        }
        Log.d("apng", "size: ${drawable?.allocationByteCount} byte")
    }

    private fun mutate() {
        drawable?.mutate()
    }

    private fun duplicate() {
        drawable = drawable?.constantState?.newDrawable() as? ApngDrawable ?: return
        drawable?.loopCount = 5
        drawable?.registerAnimationCallback(animationCallback)
        drawable?.registerRepeatAnimationCallback(animationCallback)
        drawable?.setTargetDensity(resources.displayMetrics)

        (imageView.drawable as? ApngDrawable)?.recycle()
        imageView.setImageDrawable(drawable)
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

    private fun seekTo(time: Long) {
        drawable?.seekTo(time)
    }

    private abstract class AnimationCallbacks
        : Animatable2Compat.AnimationCallback(), RepeatAnimationCallback
}
