package image.dithering

import image.Color
import image.ColorData

/**
 * A [DiffusionApproach] implementing the Floyd-Steinberg error diffusion
 * algorithm
 */
object FloydSteinbergDiffusion : DiffusionApproach {
    override fun diffuse(x: Int, y: Int, error: Color, data: ColorData) {
        if (x < data.width - 1)
            data.setColor(x + 1, y) {
                it.plus(error.times(7.0 / 16.0))
            }

        if (x > 0 && y < data.height - 1)
            data.setColor(x - 1, y + 1) {
                it.plus(error.times(3.0 / 16.0))
            }

        if (y < data.height - 1)
            data.setColor(x, y + 1) {
                it.plus(error.times(5.0 / 16.0))
            }

        if (x < data.width - 1 && y < data.height - 1)
            data.setColor(x + 1, y + 1) {
                it.plus(error.times(1.0 / 16.0))
            }
    }
}