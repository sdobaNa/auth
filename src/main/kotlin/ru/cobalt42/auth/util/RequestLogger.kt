package ru.cobalt42.auth.util

import org.springframework.web.util.ContentCachingRequestWrapper
import ru.cobalt42.auth.security.RequestProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletResponse

fun writeLog(
    httpServletRequest: ContentCachingRequestWrapper,
    httpServletResponse: HttpServletResponse,
    requestProvider: RequestProvider,
    exceptionMessage: String = "",
) {
    try {

        val dir = File(
            System.getProperty("user.dir"),
            "Logs"
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss"
        )

        val dateFormatFileName = SimpleDateFormat(
            "yyyy-MM"
        )
        val date = Date()

        val filename: String = dateFormatFileName.format(date)

        var numberOfFile = 1
        // Write the file into the folder
        var reportFile = File(dir, filename + "_" + numberOfFile + ".log")

        while (reportFile.length() > 10485760) {
            numberOfFile++
            reportFile = File(dir, filename + "_" + numberOfFile + ".log")
        }
        val fileWriter = FileWriter(reportFile, true)

        fileWriter.append("\n")
        fileWriter.append("[INFO] ").append(dateFormat.format(date)).append(" ")
        fileWriter.append("\n")
            .append(httpServletRequest.method).append(" ")
            .append(httpServletResponse.status.toString()).append(" ")
            .append(exceptionMessage).append("")
            .append("\n")
            .append(httpServletRequest.requestURI).append(" ")
            .append("\n")
            .append(requestProvider.resolveToken(httpServletRequest)).append(" ")
            .append("\n")
            .append(extractPostRequestBody(httpServletRequest))
            .append("\n")
        fileWriter.flush()
        fileWriter.close()
    } catch (_: Exception) {
    }
}

fun extractPostRequestBody(request: ContentCachingRequestWrapper): String? {
    if ("POST".equals(request.method, ignoreCase = true)) {
        val s = Scanner(request.contentAsByteArray.inputStream(), "UTF-8").useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }
    return ""
}