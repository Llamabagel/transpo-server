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

package ca.llamabagel.transpo.server.trips

import ca.llamabagel.transpo.models.trips.ApiResponse
import ca.llamabagel.transpo.server.Keys
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.routing.Routing
import io.ktor.routing.get
import javax.xml.parsers.DocumentBuilderFactory

fun Routing.trips() {
    get("trips") {
        val code = context.parameters["stop"]
        val client = HttpClient(Apache)
        val apiResponse = client.get<String>("http://api.octranspo1.com/v1.2/GetNextTripsForStopAllRoutes?appId=${Keys.OC_TRANSPO_APP_ID}&apiKey=${Keys.OC_TRANSPO_API_KEY}&stopNo=$code")

        buildResultFromResponse(apiResponse)
    }
}

/**
 * Builds an [ApiResponse] object based on the (xml) response from a request to the OC Transpo API.
 *
 * @param response The string of the response from the OC Transpo API.
 */
private fun buildResultFromResponse(response: String): ApiResponse {

    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = documentBuilder.parse(response)

    TODO()
}