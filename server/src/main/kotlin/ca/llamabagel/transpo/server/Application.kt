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