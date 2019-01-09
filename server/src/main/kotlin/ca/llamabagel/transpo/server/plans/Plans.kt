/*
 * Copyright (c) 2018 Llamabagel.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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