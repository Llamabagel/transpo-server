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