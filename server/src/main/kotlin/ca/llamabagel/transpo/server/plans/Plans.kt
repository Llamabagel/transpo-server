/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.plans

import ca.llamabagel.transpo.models.plans.request.Location
import ca.llamabagel.transpo.models.plans.request.PlansRequest
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.http.HttpMethod
import io.ktor.request.receive
import io.ktor.routing.Routing
import io.ktor.routing.post

fun Routing.plans() {
    post("/plans") {
        val obj = call.receive<PlansRequest>()

        val placesData = obj.locations.map { location ->
            when (location) {
                is Location.PlaceLocation -> getPlaceDataFromId(location.placeId, location.description == null, location.description)
                is Location.LatLngLocation -> getPlaceDataFromLatLng(location.latLng, location.description == null, location.description)
                is Location.StopLocation -> getPlaceDataFromLatLng(location.stop.getLocation(), false, location.stop.name)
            }
        }

        val client = HttpClient(Apache)
        client.call {
            header("User-Agent", "Route 613")
            method = HttpMethod.Post
        }

        println(obj)
    }
}

private fun makeRequest() = object {

}