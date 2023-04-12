package image.palette

import image.Color
import kotlin.random.Random

class RandomPalette(size: Int = 16) : Palette {
    override val values = List(size) {
        Color(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
    }

    private val palette =
        mapOf(*BuiltinPalette.values.mapIndexed { i, color -> color to i }.toTypedArray())

    override fun index(color: Color) =
        palette[color]
}