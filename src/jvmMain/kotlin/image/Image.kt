package image

import com.sksamuel.scrimage.ImmutableImage
import image.dithering.AtkinsonDiffusion
import image.dithering.BufferedImageDitheringOp
import image.dithering.FloydSteinbergDiffusion
import image.palette.BuiltinPalette
import image.palette.RandomPalette
import java.nio.file.Path

object Image {
    const val WIDTH  = 600
    const val HEIGHT = 448

    fun convert(path: Path): ImmutableImage =
        ImmutableImage.loader().fromPath(path)
            .let {
                it.op(BufferedImageDitheringOp(it.awt().createGraphics().renderingHints, BuiltinPalette, AtkinsonDiffusion))
            }
}