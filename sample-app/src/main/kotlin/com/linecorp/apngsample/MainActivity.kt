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
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.linecorp.apng.ApngDrawable
import com.linecorp.apng.RepeatAnimationCallback
import com.linecorp.apng.decoder.ApngException
import com.linecorp.apngsample.databinding.ActivityMainBinding
import com.linecorp.lich.lifecycle.AutoResetLifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val lifecycleScope: CoroutineScope = AutoResetLifecycleScope(this)

    private var drawable: ApngDrawable? = null

    @SuppressLint("SetTextI18n")
    private val animationCallback = object : AnimationCallbacks() {
        override fun onAnimationStart(drawable: Drawable) {
            Log.d("apng", "Animation start")
            binding.textCallback.text = "Animation started"
        }

        override fun onAnimationRepeat(drawable: ApngDrawable, nextLoopIndex: Int) {
            val loopCount = drawable.loopCount
            Log.d("apng", "Animation repeat loopCount: $loopCount, nextLoopIndex: $nextLoopIndex")
            binding.textCallback.text = "Animation repeat " +
                "loopCount: $loopCount, " +
                "nextLoopIndex: $nextLoopIndex"
        }

        override fun onAnimationEnd(drawable: Drawable) {
            Log.d("apng", "Animation end")
            binding.textCallback.text = "Animation ended"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLoadImage1.setOnClickListener { startLoad("test.png") }
        binding.buttonLoadImage15x.setOnClickListener { startLoad("test.png", 500, 500) }
        binding.buttonLoadImage110x.setOnClickListener { startLoad("test.png", 1000, 1000) }
        binding.buttonLoadImage2NormalPng.setOnClickListener { startLoad("normal_png.png") }
        binding.buttonLoadImage2Jpeg.setOnClickListener { startLoad("jpeg.jpg") }
        binding.buttonMutate.setOnClickListener { mutate() }
        binding.buttonCopy.setOnClickListener { duplicate() }

        binding.buttonStart.setOnClickListener { startAnimation() }
        binding.buttonStop.setOnClickListener { stopAnimation() }
        binding.buttonGc.setOnClickListener { runGc() }
        binding.buttonSeekStart.setOnClickListener { seekTo(0L) }
        binding.buttonSeekEnd.setOnClickListener { seekTo(10000000L) }
        binding.buttonSaveCurrentFrame.setOnClickListener { exportCurrentFrame() }
        binding.buttonRemove.setOnClickListener { removeView() }
    }

    @SuppressLint("SetTextI18n")
    private fun startLoad(name: String, width: Int? = null, height: Int? = null) {
        //drawable?.recycle()
        drawable?.clearAnimationCallbacks()
        drawable = null
        binding.imageView.setImageDrawable(null)
        binding.textCallback.text = null
        val isApng = assets.open(name).buffered().use {
            ApngDrawable.isApng(it)
        }
        binding.textStatus.text = "isApng: $isApng"
        if (isApng) {
            try {
                drawable = ApngDrawable.decode(assets, name, width, height)
            } catch (e: ApngException) {
                binding.textCallback.text = "Failed to decode: ${e.errorCode}"
                return
            }
            drawable?.loopCount = 5
            drawable?.setTargetDensity(resources.displayMetrics)
            drawable?.registerAnimationCallback(animationCallback)
            drawable?.registerRepeatAnimationCallback(animationCallback)
            binding.imageView.setImageDrawable(drawable)
            binding.imageView.scaleType = ImageView.ScaleType.CENTER
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

        (binding.imageView.drawable as? ApngDrawable)?.recycle()
        binding.imageView.setImageDrawable(drawable)
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

    private fun exportCurrentFrame() {
        val drawableSnapshot = drawable ?: return
        lifecycleScope.launch {
            val bitmap = exportAsBitmap(drawableSnapshot)
            val savedUri = saveAsPng(bitmap)
            val toastMessage = if (savedUri != null) {
                "Saved to $savedUri"
            } else {
                "Failed to save image"
            }
            Toast.makeText(this@MainActivity, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun exportAsBitmap(drawable: Drawable): Bitmap =
        withContext(Dispatchers.Default) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.draw(canvas)
            return@withContext bitmap
        }

    private suspend fun saveAsPng(bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        val resolver = applicationContext.contentResolver
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Audio.Media.DISPLAY_NAME,
                "apng-frame-export-${System.currentTimeMillis()}.png"
            )
        }
        val uri = resolver.insert(collectionUri, contentValues) ?: return@withContext null
        resolver.openOutputStream(uri)?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return@withContext uri
    }

    // Tests whether removing the view will release the memory
    private fun removeView() {
        binding.imageView.setImageDrawable(null)
        drawable?.clearAnimationCallbacks()
        drawable?.recycle()
        drawable = null
    }

    private abstract class AnimationCallbacks
        : Animatable2Compat.AnimationCallback(), RepeatAnimationCallback
}
