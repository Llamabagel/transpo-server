package ca.llamabagel.transpo.server

import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.index() {
    get {
        val client = HttpClient(Apache)

        call.respondText("Hello World! ${Keys.OC_TRANSPO_APP_ID}", ContentType.Text.Plain)
    }
}