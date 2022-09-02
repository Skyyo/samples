package com.skyyo.samples.utils

fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}

/**
 * Scales value from old bounds [oldMin; oldMax] to new bounds [newMin; newMax]
 * maintaining the correlation.
 *
 * Useful for drawing charts/progress
 */
fun Float.scaleBetweenBounds(
    oldMin: Float,
    oldMax: Float,
    newMin: Float,
    newMax: Float
): Float {
    return (this - oldMin) * (newMax - newMin) / (oldMax - oldMin) + newMin
}
