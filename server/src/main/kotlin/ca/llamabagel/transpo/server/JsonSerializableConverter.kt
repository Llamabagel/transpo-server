/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.charset
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.jvm.javaio.toInputStream
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

class JsonSerializableConverter @UnstableDefault constructor(private val json: Json = Json.plain) : SerializableConverter() {
    override suspend fun deserialize(
        context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>,
        contentType: ContentType,
        input: ByteReadChannel,
        serializer: KSerializer<Any>
    ): Any? {
        val text = input.toInputStream().reader(contentType.charset() ?: Charsets.UTF_8).readText()
        return json.parse(serializer, text)
    }

    override suspend fun serialize(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any,
        serializer: KSerializer<Any>
    ): Any? {
        return TextContent(
            text = json.stringify(serializer, value),
            contentType = ContentType.Application.Json.withCharset(context.call.suitableCharset())
        )
    }
}