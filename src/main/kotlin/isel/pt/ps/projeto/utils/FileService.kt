package isel.pt.ps.projeto.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


@Service
class FileService {
    // Autowiring ResourceLoader for loading resources
    @Autowired
    private val resourceLoader: ResourceLoader? = null

    // Method to read a file from the resources folder
    @Throws(IOException::class)
    fun readFileFromResources(filename: String): String {
        // Getting the resource using the ResourceLoader
        val resource = resourceLoader!!.getResource("classpath:$filename")
        // Opening an InputStream to read the content of the resource
        val inputStream = resource.inputStream
        // Creating a BufferedReader to read text from the InputStream efficiently
        val reader = BufferedReader(InputStreamReader(inputStream))
        // StringBuilder to accumulate the lines read from the file
        val stringBuilder = StringBuilder()
        var line: String?
        // Reading each line from the file and appending it to the StringBuilder
        while ((reader.readLine().also { line = it }) != null) {
            stringBuilder.append(line)
        }
        // Closing the BufferedReader
        reader.close()
        // Returning the contents of the file as a string
        return stringBuilder.toString()
    }
}