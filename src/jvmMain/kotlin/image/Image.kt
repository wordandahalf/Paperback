package image

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import image.dithering.DitheringTransform
import image.dithering.FloydSteinbergDiffusion
import image.palette.BuiltinPalette
import image.palette.Palette
import java.awt.Color as AwtColor
import java.nio.file.Path

object Image {
    const val WIDTH  = 600
    const val HEIGHT = 448

    fun convert(path: Path): ImmutableImage =
        ImmutableImage.loader().fromPath(path)
            .let {
                if (it.height > it.width)
                    it.rotateRight()
                else
                    it
            }
            .fit(WIDTH, HEIGHT, AwtColor.WHITE, ScaleMethod.Bicubic, Position.Center)
            .transform(DitheringTransform(BuiltinPalette, FloydSteinbergDiffusion))
}

fun ImmutableImage.toPackedArray(palette: Palette): ByteArray {
    val buffer = ByteArray(Image.WIDTH * Image.HEIGHT / 2)

    for (i in buffer.indices) {
        val x = i.mod(Image.WIDTH / 2) * 2
        val y = i / (Image.WIDTH / 2)

        val first = palette.index(Color(this.pixel(x, y).argb.and(0x00FFFFFF)))!!
        val second = palette.index(Color(this.pixel(x + 1, y).argb.and(0x00FFFFFF)))!!

        buffer[i] = second.shl(4).or(first).toByte()
    }

    return buffer
}
