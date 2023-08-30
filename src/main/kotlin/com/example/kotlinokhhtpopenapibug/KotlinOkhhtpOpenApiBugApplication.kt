package com.example.kotlinokhhtpopenapibug

import com.example.kotlinokhhtpopenapibug.api.ReportApi
import com.example.kotlinokhhtpopenapibug.api.model.Metadata
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@SpringBootApplication
class KotlinOkhhtpOpenApiBugApplication

fun main(args: Array<String>) {
    runApplication<KotlinOkhhtpOpenApiBugApplication>(*args)
}

@Service
class ReportService() {
    val reportApi = ReportApi(
        basePath = "http://localhost:9090",
        client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
    )

    fun sendReport() {

        val file = File.createTempFile("report-${UUID.randomUUID()}", ".xml")
        file.writeText("""
            <?xml version="1.0" encoding="UTF-8" ?>
            <body>
                <value>Some Text</value>
            </body>
        """.trimIndent())

        reportApi.submitReport(Metadata("john@doe.com", "en"), file)

        file.delete()
    }
}