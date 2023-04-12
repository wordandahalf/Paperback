package image.palette

import image.Color

object BuiltinPalette : Palette {
    @JvmStatic val BLACK   = Color(0, 0, 0)
    @JvmStatic val WHITE   = Color(255, 255, 255)
    @JvmStatic val GREEN   = Color(67, 138, 28)
    @JvmStatic val BLUE    = Color(100, 64, 255)
    @JvmStatic val RED     = Color(191, 0, 0)
    @JvmStatic val YELLOW  = Color(255, 243, 56)
    @JvmStatic val ORANGE  = Color(232, 126, 0)

    override val values =
        listOf(BLACK, WHITE, GREEN, BLUE, RED, YELLOW, ORANGE)

    private val palette =
        mapOf(*values.mapIndexed { i, color -> color to i }.toTypedArray())

    override fun index(color: Color) =
        palette[color]
}