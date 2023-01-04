package image.dithering

import image.Color
import image.palette.Palette
import java.awt.RenderingHints
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ColorModel

/**
 * A [BufferedImageOp] for converting an image's color space to that of the provided [palette]
 * using dithering with a [diffusion] approach
 */
class BufferedImageDitheringOp(
    private val hints: RenderingHints,
    private val palette: Palette,
    private val diffusion: DiffusionApproach
) : BufferedImageOp {
    @Suppress("NAME_SHADOWING")
    override fun filter(src: BufferedImage, dest: BufferedImage?): BufferedImage {
        val dest = dest ?: createCompatibleDestImage(src, src.colorModel)

        // Copy data from source
        dest.raster.setRect(src.raster)

        for (y in 0 until src.height) {
            for (x in 0 until src.width) {
                val old = Color(dest.getRGB(x, y))
                val new = palette.nearest(old)

                dest.setRGB(x, y, new.rgb)
                diffusion.diffuse(x, y, old - new, dest)
            }
        }

        return dest
    }

    override fun getBounds2D(src: BufferedImage): Rectangle2D =
        src.raster.bounds

    override fun createCompatibleDestImage(src: BufferedImage, destCM: ColorModel?): BufferedImage {
        val model = destCM ?: src.colorModel

        return BufferedImage(
            model,
            model.createCompatibleWritableRaster(src.width, src.height),
            model.isAlphaPremultiplied,
            null
        )
    }

    override fun getPoint2D(srcPt: Point2D, dstPt: Point2D) =
        dstPt.also { it.setLocation(srcPt) }

    override fun getRenderingHints() = hints
}