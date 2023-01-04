import Palette.Companion.color
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.pixels.Pixel
import java.awt.RenderingHints
import java.awt.Color as AwtColor
import java.nio.file.Path
import kotlin.math.pow

object Image {
    const val WIDTH  = 600
    const val HEIGHT = 448

    fun convert(path: Path) =
        ImmutableImage.loader().fromPath(path)
            .let {
                it.op(FloydSteinbergDitheringOp(it.awt().createGraphics().renderingHints))
            }
//            .scaleToWidth(WIDTH, ScaleMethod.Lanczos3)
//            .resizeTo(WIDTH, HEIGHT, Position.Center, AwtColor.WHITE)
//            .also { img ->
//                img.op()
//
//                img.mapInPlace {
//                    val x = it.x
//                    val y = it.y
//
//                    val current = it.color()
//                    val nearest = Palette.closest(it.color()).color
//                    val error = current - nearest
//
//                    if (x < img.width - 1)
//                        img.setColor(x + 1, y) {
//                            val diffusion = error.times(7.0 / 16.0)
//
//                            println(diffusion)
//                            (it.color() + diffusion).scrimage
//                        }
//
////                    if (x > 0 && y < img.height - 1)
////                        img.setColor(x - 1, y + 1) {
////                            val diffusion = error.times(3.0 / 16.0)
////
////                            (it.color() + diffusion).scrimage
////                        }
////
////                    if (y < img.height - 1)
////                        img.setColor(x, y + 1) {
////                            val diffusion = error.times(5.0 / 16.0)
////
////                            (it.color() + diffusion).scrimage
////                        }
////
////                    if (x < img.width - 1 && y < img.height - 1)
////                        img.setColor(x + 1, y + 1) {
////                            val diffusion = error.times(1.0 / 16.0)
////
////                            (it.color() + diffusion).scrimage
////                        }
//
//                    nearest.awt
//                }
//            }
}