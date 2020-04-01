package com.practice.practice.qrcode

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import javax.imageio.ImageIO

/**
 * @author Stefan Liu
 */
class QrcodeGenerator {
    companion object {
        fun generateQRcodePic(content: String, width: Int, height: Int, picFormat: String) {
            val hints = mutableMapOf<EncodeHintType, Any>(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.MARGIN to 1
            )
            try {
                val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)
                val dir = File("img")
                if (!dir.exists()) {
                    dir.mkdir()
                }
                val file = File("img/1.$picFormat").toPath()
                MatrixToImageWriter.writeToPath(bitMatrix, picFormat, file)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        fun generateQRcodeByte(content: String, width: Int, picFormat: String): ByteArray? {
            return try {
                val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, width)
                val image = toBufferedImage(bitMatrix)
                val out = ByteArrayOutputStream()
                ImageIO.write(image, picFormat, out)
                out.toByteArray()
            } catch (e: Exception) {
                null
            }
        }

        fun toBufferedImage(matrix: BitMatrix): BufferedImage {
            val width = matrix.width
            val height = matrix.height
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
            val onColor = 0xFF0000
            val offColor = 0xFFFFFF
            repeat(width) { x ->
                repeat(height) { y ->
                    image.setRGB(x, y, if (matrix.get(x, y)) onColor else offColor)
                }
            }
            return image
        }

        private const val QRCODE_SIZE = 300
        private const val CONTENT = "http://blog.csdn.net/magi1201";
        private val sf = SimpleDateFormat("yyyy-MM-dd")

        fun doMain() {
            generateQRcodePic(CONTENT, QRCODE_SIZE, QRCODE_SIZE, "jpg")
        }
    }
}

fun main() {
    QrcodeGenerator.doMain()
}

