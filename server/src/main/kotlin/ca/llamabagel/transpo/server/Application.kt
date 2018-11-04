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

import ca.llamabagel.transpo.server.trips.trips
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.routing.Routing

fun Application.main() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
    }

    install(Routing) {
        index()
        trips()
    }
}