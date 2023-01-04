import com.sksamuel.scrimage.nio.PngWriter
import image.Image
import kotlin.io.path.Path
import kotlin.test.Test

class ImageConversionTest {
    @Test
    fun testImageConversion() {
        Image.convert(Path("image.jpg"))
            .output(PngWriter.NoCompression, "converted.png")
    }
}