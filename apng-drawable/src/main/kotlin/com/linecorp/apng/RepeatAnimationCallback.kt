package com.linecorp.apng

/**
 * This interface is for receiving callback when animation is about to be repeated.
 */
interface RepeatAnimationCallback {
    /**
     * This is called when animation is about to be repeated.
     * [loopCount] is the total number of times the animation will be repeated.
     * [nextLoop] is the loop count of the next animation.
     */
    fun onRepeat(
        drawable: ApngDrawable,
        loopCount: Int,
        nextLoop: Int
    )
}
