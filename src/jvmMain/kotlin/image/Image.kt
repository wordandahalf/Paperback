package image

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import image.dithering.DitheringTransform
import image.dithering.FloydSteinbergDiffusion
import image.palette.BuiltinPalette
import java.awt.Color
import java.nio.file.Path

object Image {
    private const val WIDTH  = 600
    private const val HEIGHT = 448

    fun convert(path: Path) =
        ImmutableImage.loader().fromPath(path)
            .fit(WIDTH, HEIGHT, Color.WHITE, ScaleMethod.Bicubic, Position.Center)
            .transform(DitheringTransform(BuiltinPalette, FloydSteinbergDiffusion))
}