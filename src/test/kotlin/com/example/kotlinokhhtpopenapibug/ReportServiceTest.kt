package com.example.kotlinokhhtpopenapibug

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest
@AutoConfigureWireMock(port = 9090)
class ReportServiceTest {
    @Autowired
    private lateinit var reportService: ReportService

    @Test
    fun `submit a multipart form-data with json and a file`() {
        stubFor(
            post(urlEqualTo("/submit"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                                "statusCode": "some code"
                            }
                        """.trimIndent()
                        )
                )
        )
        reportService.sendReport()
    }
}