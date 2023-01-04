package image

import sqr
import kotlin.math.sqrt

data class Color(
    val r: Int,
    val g: Int,
    val b: Int
) {
    constructor(rgb: Int) : this(
        rgb.shr(16).and(0xFF),
        rgb.shr(8).and(0xFF),
        rgb.and(0xFF)
    )

    val rgb = r.shl(16).or(g.shl(8)).or(b)

    operator fun times(coeff: Double) =
        Color(
            r.times(coeff).toInt().coerceIn(0, 255),
            g.times(coeff).toInt().coerceIn(0, 255),
            b.times(coeff).toInt().coerceIn(0, 255),
        )

    operator fun plus(other: Color) =
        Color(
            (r + other.r).coerceIn(0, 255),
            (g + other.g).coerceIn(0, 255),
            (b + other.b).coerceIn(0, 255),
        )

    operator fun minus(other: Color) =
        Color(
            (r - other.r).coerceIn(0, 255),
            (g - other.g).coerceIn(0, 255),
            (b - other.b).coerceIn(0, 255),
        )

    fun error(other: Color) =
        sqrt((r - other.r).sqr() + (g - other.g).sqr() + (b - other.b).sqr())
}