package image.dithering

import image.Color
import java.awt.image.BufferedImage

object AtkinsonDiffusion : DiffusionApproach {
    @Suppress("NAME_SHADOWING")
    override fun diffuse(x: Int, y: Int, error: Color, image: BufferedImage) {
        val error = error.times(1.0 / 8.0)

        if (x < image.width - 1)
            image.getRGB(x + 1, y).also {
                image.setRGB(x + 1, y, Color(it).plus(error).rgb)
            }

        if (x < image.width - 2)
            image.getRGB(x + 2, y).also {
                image.setRGB(x + 2, y, Color(it).plus(error).rgb)
            }

        if (x > 0 && y < image.height - 1)
            image.getRGB(x - 1, y + 1).also {
                image.setRGB(x - 1, y + 1, Color(it).plus(error).rgb)
            }

        if (y < image.height - 1)
            image.getRGB(x, y + 1).also {
                image.setRGB(x, y + 1, Color(it).plus(error).rgb)
            }

        if (x < image.width - 1 && y < image.height - 1)
            image.getRGB(x + 1, y + 1).also {
                image.setRGB(x + 1, y + 1, Color(it).plus(error).rgb)
            }

        if (x > 0 && y < image.height - 2)
            image.getRGB(x - 1, y + 2).also {
                image.setRGB(x - 1, y + 2, Color(it).plus(error).rgb)
            }
    }
}