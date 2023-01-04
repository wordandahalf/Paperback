package image.dithering

import image.Color
import java.awt.image.BufferedImage

/**
 * A [DiffusionApproach] implementing the Floyd-Steinberg error diffusion
 * algorithm
 */
object FloydSteinbergDiffusion : DiffusionApproach {
    override fun diffuse(x: Int, y: Int, error: Color, image: BufferedImage) {
        if (x < image.width - 1)
            image.getRGB(x + 1, y).also {
                image.setRGB(x + 1, y, Color(it).plus(error.times(7.0 / 16.0)).rgb)
            }

        if (x > 0 && y < image.height - 1)
            image.getRGB(x - 1, y + 1).also {
                image.setRGB(x - 1, y + 1, Color(it).plus(error.times(3.0 / 16.0)).rgb)
            }

        if (y < image.height - 1)
            image.getRGB(x, y + 1).also {
                image.setRGB(x, y + 1, Color(it).plus(error.times(5.0 / 16.0)).rgb)
            }

        if (x < image.width - 1 && y < image.height - 1)
            image.getRGB(x + 1, y + 1).also {
                image.setRGB(x + 1, y + 1, Color(it).plus(error.times(1.0 / 16.0)).rgb)
            }
    }
}