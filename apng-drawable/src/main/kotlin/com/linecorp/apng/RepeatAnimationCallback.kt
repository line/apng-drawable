package com.linecorp.apng

/**
 * This interface is for receiving callback when animation is about to be repeated.
 */
interface RepeatAnimationCallback {
    /**
     * This is called when animation is about to be repeated.
     * @param [nextLoop] loop count of the next animation.
     */
    fun onRepeat(
        drawable: ApngDrawable,
        nextLoop: Int
    )
}
