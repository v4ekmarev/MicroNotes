package com.develop.data.network.logging

import com.develop.core.common.AppLogger
import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*

val CurlLoggingPlugin = createClientPlugin("CurlLoggingPlugin") {
    onRequest { request, content ->
        val outgoingContent = content as? OutgoingContent
        val curlCommand = buildCurlCommand(request, outgoingContent)
        
        AppLogger.d(TAG, "╭--- cURL (${request.url})")
        AppLogger.d(TAG, curlCommand)
        AppLogger.d(TAG, "╰--- (copy and paste the above line to a terminal)")
    }
}

private suspend fun buildCurlCommand(request: HttpRequestBuilder, content: OutgoingContent?): String {
    val builder = StringBuilder("curl")
    
    builder.append(" -X ${request.method.value}")
    
    var compressed = false
    request.headers.build().forEach { name, values ->
        values.forEach { value ->
            val escapedValue = escapeHeaderValue(value)
            
            if (name.equals("Accept-Encoding", ignoreCase = true) &&
                value.equals("gzip", ignoreCase = true)) {
                compressed = true
            }
            
            builder.append(" -H \"$name: $escapedValue\"")
        }
    }
    
    content?.let { outgoingContent ->
        val bodyString = extractBody(outgoingContent)
        if (bodyString.isNotEmpty()) {
            val escapedBody = bodyString
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
            builder.append(" --data $'$escapedBody'")
        }
    }
    
    if (compressed) {
        builder.append(" --compressed")
    }
    
    builder.append(" \"${request.url.buildString()}\"")
    
    return builder.toString()
}

private fun escapeHeaderValue(value: String): String {
    return if (value.startsWith("\"") && value.endsWith("\"")) {
        "\\\"${value.substring(1, value.length - 1)}\\\""
    } else {
        value.replace("\"", "\\\"")
    }
}

private suspend fun extractBody(content: OutgoingContent): String {
    return when (content) {
        is OutgoingContent.ByteArrayContent -> {
            content.bytes().decodeToString()
        }
        is OutgoingContent.ReadChannelContent -> {
            try {
                content.readFrom().readRemaining().readText()
            } catch (e: Exception) {
                "[unable to read body]"
            }
        }
        is OutgoingContent.WriteChannelContent -> {
            "[streaming body]"
        }
        is OutgoingContent.NoContent -> ""
        is OutgoingContent.ProtocolUpgrade -> "[protocol upgrade]"
        else -> "[unknown body type]"
    }
}

private const val TAG = "CurlLogging"
