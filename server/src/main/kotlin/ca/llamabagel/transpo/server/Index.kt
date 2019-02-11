/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
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