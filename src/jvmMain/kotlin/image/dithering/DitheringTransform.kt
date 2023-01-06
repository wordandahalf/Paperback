package image.dithering

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.transform.Transform
import image.ColorData
import image.palette.Palette

/**
 * A [Transform] for converting an image's color space to that of the provided [palette]
 * using dithering with a [diffusion] approach
 */
class DitheringTransform(
    private val palette: Palette,
    private val diffusion: DiffusionApproach
) : Transform {
    override fun apply(input: ImmutableImage): ImmutableImage {
        return input.apply {
            val data = ColorData(this)

            for (y in 0 until input.height) {
                for (x in 0 until input.width) {
                    val old = data.getColor(x, y)
                    val new = palette.nearest(old)

                    data.setColor(x, y, new)
                    diffusion.diffuse(x, y, old - new, data)
                }
            }
        }
    }
}