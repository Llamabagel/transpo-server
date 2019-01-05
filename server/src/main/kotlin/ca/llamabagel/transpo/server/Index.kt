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

import ca.llamabagel.transpo.models.app.Stop
import ca.llamabagel.transpo.models.plans.request.Location
import ca.llamabagel.transpo.models.plans.request.PlansRequest
import ca.llamabagel.transpo.models.plans.request.locationTypeFactory
import ca.llamabagel.transpo.models.plans.response.StepDetails
import ca.llamabagel.transpo.models.plans.response.stepDetailsTypeFactory
import com.google.gson.GsonBuilder
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.util.*

fun Routing.index() {
    get {
        val client = HttpClient(Apache)

        //call.respondText("Hello World! ${Keys.OC_TRANSPO_APP_ID}", ContentType.Text.Plain)

        val obj = PlansRequest(
                listOf(
                        Location.StopLocation(Stop("AA100", "1000", "A Stop", -75.0, 45.0)),
                        Location.PlaceLocation("abcdefg", "A Place")
                )
        )

        val json = GsonBuilder()
                .setDateFormat("MMM d, yyyy HH:mm:ss zzz")
                .registerTypeAdapterFactory(stepDetailsTypeFactory)
                .registerTypeAdapterFactory(locationTypeFactory)
                .create()
                .toJson(obj)

        call.respondText(json, ContentType.Text.Plain)
    }
}