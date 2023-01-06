package image

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import java.awt.image.BufferedImage

@JvmInline
value class ColorData(
    val backing: ImmutableImage
) {
    val width: Int
        get() = backing.width

    val height: Int
        get() = backing.height

    fun getColor(x: Int, y: Int) =
        Color(backing.pixel(x, y).argb.and(0xFFFFFF))

    fun setColor(x: Int, y: Int, color: Color) {
        backing.setColor(x, y, RGBColor(color.r, color.g, color.b))
    }

    fun setColor(x: Int, y: Int, apply: (Color) -> Color) {
        backing.setColor(x, y, apply.invoke(getColor(x, y)).let { RGBColor(it.r, it.g, it.b) })
    }
}