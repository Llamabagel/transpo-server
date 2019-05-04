/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import ca.llamabagel.transpo.server.data.data
import ca.llamabagel.transpo.server.plans.plans
import ca.llamabagel.transpo.server.trips.trips
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.routing.Routing

fun Application.main() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        //register(ContentType.Any, JsonSerializableConverter())
        gson {  }
    }

    install(Routing) {
        index()
        trips()
        plans()
        data()
    }
}