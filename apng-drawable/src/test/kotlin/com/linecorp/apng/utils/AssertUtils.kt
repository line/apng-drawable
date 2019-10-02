package com.linecorp.apng.utils

import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.fail

/**
 * Asserts that a [block] operation fails with an exception, where the type is [T] and the message is [failureMessage].
 */
inline fun <reified T : Throwable> assertFailureMessage(failureMessage: String, block: () -> Unit) {
    try {
        block()
        fail("An exception was expected but executed without exception.")
    } catch (throwable: Throwable) {
        assertThat(throwable, instanceOf(T::class.java))
        assertEquals(failureMessage, throwable.message)
    }
}
