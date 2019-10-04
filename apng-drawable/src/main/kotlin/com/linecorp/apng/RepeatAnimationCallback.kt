package com.linecorp.apng

/**
 * This interface is for receiving callback when animation is about to be repeated.
 */
interface RepeatAnimationCallback {
    /**
     * This is called when animation is about to be repeated.
     * @param [nextLoop] loop count of the next animation.
     */
    @Deprecated(
        message = "Use onAnimationRepeat",
        replaceWith = ReplaceWith("")
    )
    fun onRepeat(
        drawable: ApngDrawable,
        nextLoop: Int
    ) = Unit

    /**
     * This is called when animation is about to be repeated.
     *
     * TODO: This function has a default implementation for backward compatibility.
     *  It will be removed at the next version.
     *
     * @param [nextLoopIndex] loop index of the next animation. e.g. If [nextLoopIndex] equals to
     *  `[ApngDrawable.loopCount] - 1`, the last loop will be started.
     */
    fun onAnimationRepeat(
        drawable: ApngDrawable,
        nextLoopIndex: Int
    ) = Unit
}
