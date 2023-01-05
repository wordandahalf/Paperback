package image.dithering

import image.Color
import image.ColorData

/**
 * Applies error diffusion to an image being dithered
 */
interface DiffusionApproach {
    fun diffuse(x: Int, y: Int, error: Color, data: ColorData)
}