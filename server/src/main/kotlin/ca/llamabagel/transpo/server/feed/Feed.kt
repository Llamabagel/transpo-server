/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.feed

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.response.readBytes
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.feed() {
    get("trips") {
        val language = context.parameters["lang"] ?: "en"

        val client = HttpClient(Apache)
        val rssBytes = client.call("http://www.octranspo.com/feeds/updates-$language/")
                .response
                .readBytes()
    }
}