package ca.llamabagel.transpo.server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.routing.routing

fun Application.main() {
    install(DefaultHeaders)

    routing {
        index()
    }
}