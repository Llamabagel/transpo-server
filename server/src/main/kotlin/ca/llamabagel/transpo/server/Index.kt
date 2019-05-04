/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server

import ca.llamabagel.transpo.models.plans.request.Location
import ca.llamabagel.transpo.models.plans.request.PlansRequest
import ca.llamabagel.transpo.models.transit.Stop
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.application.call
import io.ktor.response.respond


fun Routing.index() {
    get {
        val client = HttpClient(Apache)

        //call.respondText("Hello World! ${Keys.OC_TRANSPO_APP_ID}", ContentType.Text.Plain)

        val obj = PlansRequest(
            listOf(
                Location.StopLocation(Stop("AA100", "1000", "A Stop", -75.0, 45.0, 0, null)),
                Location.PlaceLocation("abcdefg", "A Place")
            )
        )

        call.respond(obj)
    }
}