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
import kotlin.math.ceil

object Image {
    const val WIDTH  = 600
    const val HEIGHT = 448

    /*
        // size = width x height x bitdepth
        //      = 600 x 448 x 4 = 1,075,200 bits = 143,400 bytes
     */
    fun convert(path: Path): ImmutableImage =
        ImmutableImage.loader().fromPath(path)
            .fit(WIDTH, HEIGHT, AwtColor.WHITE, ScaleMethod.Bicubic, Position.Center)
            .transform(DitheringTransform(BuiltinPalette, FloydSteinbergDiffusion))
}

fun ImmutableImage.toPackedArray(palette: Palette): ByteArray {
    val length = this.width.times(this.height)
    val data = ByteArray(length)

    for (i in 0 until length step 2) {
        val x = i.mod(Image.WIDTH)
        val y = i.floorDiv(Image.HEIGHT)

        if (x >= this.width)
            continue

        if (y >= this.height)
            continue

        val first = palette.index(Color(this.pixel(x, y).argb.and(0x00FFFFFF)))!!
        val second = palette.index(Color(this.pixel(x + 1, y).argb.and(0x00FFFFFF)))!!

        data[i.floorDiv(2)] = first.shl(8).or(second).toByte()
    }

    return data
}
