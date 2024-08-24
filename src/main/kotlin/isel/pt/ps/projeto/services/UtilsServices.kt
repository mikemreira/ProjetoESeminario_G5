package isel.pt.ps.projeto.services

import org.springframework.stereotype.Component
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Base64
import javax.imageio.ImageIO


@Component
class UtilsServices {

    fun base64ToByteArray(base64String: String): ByteArray {
        var base64Image = base64String
        if (base64String.startsWith("data:image/jpeg;base64,")) {
            base64Image = base64String.substringAfter("data:image/jpeg;base64,")
        }
        return Base64.getDecoder().decode(base64Image)
    }

    fun resizeAndCompressImageBase64(foto: String, newWidth: Int, newHeight: Int, quality: Float): ByteArray {
        // Decode the base64 string to a byte array
        val imageBytes = base64ToByteArray(foto)

        // Convert byte array to BufferedImage
        val inputStream = ByteArrayInputStream(imageBytes)
        val originalImage = ImageIO.read(inputStream)

        // Resize the image
        val resizedImage = BufferedImage(newWidth, newHeight, originalImage.type)
        val g2d: Graphics2D = resizedImage.createGraphics()
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
        g2d.dispose()

        // Set compression quality
        val outputStream = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName("jpeg")
        val writer = writers.next()
        val ios = ImageIO.createImageOutputStream(outputStream)
        writer.output = ios

        val param = writer.defaultWriteParam
        if (param.canWriteCompressed()) {
            param.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
            param.compressionQuality = quality
        }

        writer.write(null, javax.imageio.IIOImage(resizedImage, null, null), param)
        ios.close()
        writer.dispose()

        return outputStream.toByteArray()
    }

    fun encodeToBase64(byteArray: ByteArray): String {
        return Base64.getEncoder().encodeToString(byteArray)
    }


    fun isValidLocalDate(dateStr: String, format: String = "yyyy-MM-dd"): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern(format)
            LocalDate.parse(dateStr, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }




}