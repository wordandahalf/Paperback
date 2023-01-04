package image.palette

import image.Color
import kotlin.random.Random

class RandomPalette(size: Int = 16) : Palette {
    override val values = Array(size) {
        Color(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255))
    }
}