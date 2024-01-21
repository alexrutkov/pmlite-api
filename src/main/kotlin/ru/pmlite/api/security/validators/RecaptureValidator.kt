package ru.pmlite.api.security.validators

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.pmlite.api.security.dto.RecaptureResult
import ru.pmlite.api.security.dto.ValidateResult
import ru.pmlite.api.security.exceptions.RecaptureValidException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets

@Service
class RecaptureValidator(
    @Value("\${app.recapture.secret-key}")
    private val secretKey: String,
    private val httpClient: HttpClient,
    private val mapper: ObjectMapper
) {
    private val commonRequest = HttpRequest.newBuilder()
        .uri(URI.create("https://www.google.com/recaptcha/api/siteverify"))
        .header("Content-Type", "application/x-www-form-urlencoded")
    fun validate(recaptcha: String): ValidateResult {
        val params = mapOf("response" to recaptcha, "secret" to secretKey)
            .let(this::getFormDataAsString)
        val result = commonRequest.copy().POST(BodyPublishers.ofString(params)).build()
            .let { request ->  httpClient.send(request, BodyHandlers.ofString()) }
            .let { response -> mapper.readValue<RecaptureResult>(response.body()) }
        return if (result.success) ValidateResult(true)
        else throw RecaptureValidException(result.errorCodes?.joinToString() ?: "Ошибка reCapture!")
    }

    private fun getFormDataAsString(formData: Map<String, String>): String {
        val formBodyBuilder = StringBuilder()
        for ((key, value) in formData) {
            if (formBodyBuilder.isNotEmpty()) { formBodyBuilder.append("&") }
            formBodyBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
            formBodyBuilder.append("=")
            formBodyBuilder.append(URLEncoder.encode(value, StandardCharsets.UTF_8))
        }
        return formBodyBuilder.toString()
    }
}
