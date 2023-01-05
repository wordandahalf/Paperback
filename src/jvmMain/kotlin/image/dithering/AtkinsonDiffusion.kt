package image.dithering

import image.Color
import image.ColorData

object AtkinsonDiffusion : DiffusionApproach {
    @Suppress("NAME_SHADOWING")
    override fun diffuse(x: Int, y: Int, error: Color, data: ColorData) {
        val error = error.times(1.0 / 8.0)

        if (x < data.width - 1)
            data.setColor(x + 1, y) { it.plus(error) }

        if (x < data.width - 2)
            data.setColor(x + 2, y) { it.plus(error) }

        if (x > 0 && y < data.height - 1)
            data.setColor(x - 1, y + 1) { it.plus(error) }

        if (y < data.height - 1)
            data.setColor(x, y + 1) { it.plus(error) }

        if (x < data.width - 1 && y < data.height - 1)
            data.setColor(x + 1, y + 1) { it.plus(error) }

        if (x > 0 && y < data.height - 2)
            data.setColor(x - 1, y + 2) { it.plus(error) }
    }
}