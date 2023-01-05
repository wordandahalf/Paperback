package image

import java.awt.image.BufferedImage

@JvmInline
value class ColorData(
    val backing: BufferedImage
) {
    val width: Int
        get() = backing.width

    val height: Int
        get() = backing.height

    fun getColor(x: Int, y: Int) =
        Color(backing.getRGB(x, y))

    fun setColor(x: Int, y: Int, color: Color) {
        backing.setRGB(x, y, color.rgb)
    }

    fun setColor(x: Int, y: Int, apply: (Color) -> Color) {
        backing.setRGB(x, y, apply.invoke(Color(backing.getRGB(x, y))).rgb)
    }
}