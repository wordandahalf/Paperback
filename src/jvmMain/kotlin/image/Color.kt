package image

import sqr
import kotlin.math.sqrt

@JvmInline
value class Color(
    val rgb: Int
) {
    constructor(r: Int, g: Int, b: Int) : this(
        r.shl(16).or(g.shl(8)).or(b)
    )

    val r: Int
        get() = rgb.shr(16).and(0xFF)
    val g: Int
        get() = rgb.shr(8).and(0xFF)
    val b: Int
        get() = rgb.and(0xFF)

    fun error(other: Color) =
        sqrt((r - other.r).sqr() + (g - other.g).sqr() + (b - other.b).sqr())

    operator fun plus(other: Color) =
        Color(
            r.plus(other.r).coerceIn(0, 255).shl(16)
                .or(g.plus(other.g).coerceIn(0, 255).shl(8))
                .or(b.plus(other.b).coerceIn(0, 255))
        )

    operator fun minus(other: Color) =
        Color(
            r.minus(other.r).coerceIn(0, 255).shl(16)
                .or(g.minus(other.g).coerceIn(0, 255).shl(8))
                .or(b.minus(other.b).coerceIn(0, 255))
        )

    operator fun times(coeff: Double) =
        Color(
            r.times(coeff).toInt().coerceIn(0, 255).shl(16)
                .or(g.times(coeff).toInt().coerceIn(0, 255).shl(8))
                .or(b.times(coeff).toInt().coerceIn(0, 255))
        )
}