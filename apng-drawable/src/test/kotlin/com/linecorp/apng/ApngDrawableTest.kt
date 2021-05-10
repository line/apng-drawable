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

package com.linecorp.apng

import android.graphics.Canvas
import android.graphics.Rect
import com.linecorp.apng.decoder.Apng
import com.linecorp.apng.utils.assertFailureMessage
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

/**
 * Test for [ApngDrawable].
 */
@RunWith(RobolectricTestRunner::class)
class ApngDrawableTest {

    @Mock
    private lateinit var nBitmapAnimation: Apng

    @Mock
    private lateinit var canvas: Canvas
    private lateinit var currentTimeProvider: CurrentTimeProvider
    private lateinit var target: ApngDrawable

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(nBitmapAnimation.width).thenReturn(200)
        whenever(nBitmapAnimation.height).thenReturn(100)
        whenever(nBitmapAnimation.frameCount).thenReturn(10)
        whenever(nBitmapAnimation.frameDurations).thenReturn(
            intArrayOf(10, 10, 5, 20, 5, 10, 10, 10, 10, 10)
        )
        whenever(nBitmapAnimation.duration).thenReturn(100)
        whenever(nBitmapAnimation.loopCount).thenReturn(2)
        currentTimeProvider = CurrentTimeProvider(0L)
        target = ApngDrawable(
            ApngDrawable.ApngState(
                nBitmapAnimation,
                200,
                100
            ) {
                currentTimeProvider.provide()
            }
        )
    }

    @Test
    fun testDraw_withStopCondition() {
        target.stop()

        currentTimeProvider.currentTimeMillis = 0L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(0),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 10L
        target.draw(canvas)
        verify(nBitmapAnimation, times(2)).drawWithIndex(
            eq(0),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )
    }

    @Test
    fun testDraw_withStartCondition() {
        target.start()

        currentTimeProvider.currentTimeMillis = 0L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(0),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 10L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(1),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 24L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(2),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 25L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(3),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 99L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(9),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 100L
        target.draw(canvas)
        verify(nBitmapAnimation, times(2)).drawWithIndex(
            eq(0),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )
    }

    @Test
    fun testDraw_withRepeat() {
        target.start()

        currentTimeProvider.currentTimeMillis = 0L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(0),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 10L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(1),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        currentTimeProvider.currentTimeMillis = 110L
        target.draw(canvas)
        verify(nBitmapAnimation, times(2)).drawWithIndex(
            eq(1),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )

        // Because repeat count is exceeded, so shows last frame.
        currentTimeProvider.currentTimeMillis = 210L
        target.draw(canvas)
        verify(nBitmapAnimation, times(1)).drawWithIndex(
            eq(9),
            eq(canvas),
            anyOrNull(),
            eq(Rect(0, 0, 200, 100)),
            any()
        )
    }

    @Test
    fun testSeekToFrame() {
        target.seekToFrame(0, 0)
        assertEquals(0, target.currentLoopIndex)
        assertEquals(0, target.currentFrameIndex)

        target.seekToFrame(0, 3)
        assertEquals(0, target.currentLoopIndex)
        assertEquals(3, target.currentFrameIndex)

        target.seekToFrame(1, 0)
        assertEquals(1, target.currentLoopIndex)
        assertEquals(0, target.currentFrameIndex)

        target.seekToFrame(1, 3)
        assertEquals(1, target.currentLoopIndex)
        assertEquals(3, target.currentFrameIndex)

        target.seekToFrame(1, 9)
        assertEquals(1, target.currentLoopIndex)
        assertEquals(9, target.currentFrameIndex)
    }

    @Test
    fun testSeekToFrame_error() {
        assertFailureMessage<IllegalArgumentException>("loopIndex must be positive value") {
            target.seekToFrame(-1, 0)
        }

        assertFailureMessage<IllegalArgumentException>("frameIndex must be positive value") {
            target.seekToFrame(0, -1)
        }

        assertFailureMessage<IllegalArgumentException>(
            "loopIndex must be less than loopCount. loopIndex = 3, loopCount = 2."
        ) {
            target.seekToFrame(3, 0)
        }

        assertFailureMessage<IllegalArgumentException>(
            "frameIndex must be less than frameCount. frameIndex = 11, frameCount = 10."
        ) {
            target.seekToFrame(0, 11)
        }
    }

    @Test
    fun testFrameDurations() {
        assertEquals(target.frameDurations, listOf(10, 10, 5, 20, 5, 10, 10, 10, 10, 10))
    }

    private class CurrentTimeProvider(var currentTimeMillis: Long) {
        fun provide(): Long = currentTimeMillis
    }
}
