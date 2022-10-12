package com.example

import com.example.formats.JacksonMessage
import com.example.formats.jacksonMessageLens
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

val app: HttpHandler = routes(
        "/ping" bind GET to {
            Response(OK).body("pong")
        },

        "/formats/json/jackson" bind GET to {
            jacksonHandler()
        },

        "/testing/kotest" bind GET to { request ->
            Response(OK).body("Echo '${request.bodyString()}'")
        },

        "/health/status" bind GET to {
            Response(OK).body("UP")
        }
)

private fun jacksonHandler() = Response(OK)
        .with(jacksonMessageLens of JacksonMessage("Barry", "Hello there!"))

fun main() {
    //Filters could be added in between. Similar to interceptor
    val printingApp: HttpHandler = PrintRequest()
            .then(DebuggingFilters.PrintResponse())
            .then(app)

    val server = printingApp.asServer(Jetty(9000)).start()

    println("Server started on " + server.port())
}
