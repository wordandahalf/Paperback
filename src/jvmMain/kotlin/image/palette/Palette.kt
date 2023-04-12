package image.palette

import image.Color

/**
 * A collection of colors available for display
 */
interface Palette {
    /**
     * The [Color]s in this [Palette]
     */
    val values: Collection<Color>

    fun index(color: Color): Int?

    /**
     * @return the [Color] in [values] nearest to the provided [color]
     */
    open fun nearest(color: Color) =
        values.minByOrNull {
            color.error(it)
        }!!
}