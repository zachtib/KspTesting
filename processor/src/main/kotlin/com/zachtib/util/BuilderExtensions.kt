package com.zachtib.util

/**
 * Apply [action] for each element in [elements] to the receiver
 * before returning it.
 *
 * [elements] accepts a null-value
 *
 * This is just meant as a convenience function for writing (I hope)
 * readable code working with builder patterns.
 *
 * @param T
 * @param E
 * @receiver
 * @param elements A collection of E, or null
 * @param action
 * @return
 */
fun <T : Any, E : Any> T.applyForEach(
    elements: Collection<E>?,
    action: T.(E) -> Unit
): T {
    if (elements != null) {
        for (element in elements) {
            action(element)
        }
    }
    return this
}