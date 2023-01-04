package image.dithering

import image.Color
import java.awt.image.BufferedImage

/**
 * Applies error diffusion to an image being dithered
 */
interface DiffusionApproach {
    fun diffuse(x: Int, y: Int, error: Color, image: BufferedImage)
}