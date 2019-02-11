/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import ca.llamabagel.transpo.models.plans.request.Location
import ca.llamabagel.transpo.models.plans.request.locationTypeFactory
import ca.llamabagel.transpo.models.plans.response.StepDetails
import ca.llamabagel.transpo.models.plans.response.stepDetailsTypeFactory
import ca.llamabagel.transpo.server.plans.plans
import ca.llamabagel.transpo.server.trips.trips
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.routing.Routing

fun Application.main() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            setDateFormat("MMM d, yyyy HH:mm:ss zzz")
            setPrettyPrinting()
            registerTypeAdapterFactory(stepDetailsTypeFactory)
            registerTypeAdapterFactory(locationTypeFactory)
        }
    }

    install(Routing) {
        index()
        trips()
        plans()
    }
}