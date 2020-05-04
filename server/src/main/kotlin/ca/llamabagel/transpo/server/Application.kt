/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import ca.llamabagel.transpo.server.data.data
import ca.llamabagel.transpo.server.feed.LiveUpdatesCacher
import ca.llamabagel.transpo.server.feed.feed
import ca.llamabagel.transpo.server.plans.plans
import ca.llamabagel.transpo.server.trips.trips
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.routing.Routing
import io.ktor.serialization.json
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.launch

val Config by lazy { Configuration("config") }


@ExperimentalStdlibApi
fun main() {
    val server = embeddedServer(CIO, port = 8080) {
        install(DefaultHeaders)
        install(ContentNegotiation) {
            json()
        }

        install(Routing) {
            index()
            trips()
            plans()
            data()
            feed()
        }

        launch {
            LiveUpdatesCacher().beginUpdates()
        }
    }
    server.start(wait = true)
}