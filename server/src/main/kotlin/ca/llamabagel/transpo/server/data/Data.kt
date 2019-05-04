/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.data

import ca.llamabagel.transpo.Configuration
import ca.llamabagel.transpo.models.app.AppMetadata
import ca.llamabagel.transpo.models.app.DataPackage
import ca.llamabagel.transpo.models.app.MetadataRequest
import ca.llamabagel.transpo.models.app.Version
import ca.llamabagel.transpo.server.DataSource
import ca.llamabagel.transpo.server.GenericError
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.io.File

fun Routing.data() {
    post("data/metadata") {
        val requestData = call.receive<MetadataRequest>()

        val metaData = getMetadata(requestData.platform)
        if (metaData != null) {
            call.respond(metaData)
        } else {
            call.respond(HttpStatusCode.InternalServerError, GenericError(404, "Could not load metadata"))
        }
    }

    get("data/{platform}/{schema}/{v?}") {
        var version = context.parameters["v"]
        val schema = context.parameters["schema"]?.toIntOrNull() ?: 1
        val platform = context.parameters["platform"]!!

        if (version == null) {
            val metadata = getMetadata(platform)
            if (metadata == null) {
                call.respond(HttpStatusCode.InternalServerError, Any())
                return@get
            }

            version = metadata.dataVersion.value
        }

        val file = File("${Configuration.DATA_PACKAGE_DIRECTORY}/$schema/$version/$version.json")
        if (file.exists()) {
            call.respondFile(file)
        } else {
            call.respond(HttpStatusCode.NotFound, GenericError(404, "Data file not found"))
        }
    }
}