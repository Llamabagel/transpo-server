/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.feed

import ca.llamabagel.transpo.models.updates.AffectedStop
import ca.llamabagel.transpo.models.updates.LiveUpdate
import ca.llamabagel.transpo.server.DataSource
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.sql.Connection
import java.sql.ResultSet
import java.util.*

fun Routing.feed() {
    get("feed") {
        val language = context.parameters["lang"] ?: "en"

        DataSource.getConnection().use { connection ->
            val statement =
                connection.prepareStatement("SELECT * FROM live_updates WHERE language = ? AND active = true").apply {
                    setString(1, language)
                }

            val resultSet = statement.executeQuery()
            val results = generateSequence {
                if (resultSet.next()) getLiveUpdateFromResultSet(
                    connection,
                    resultSet
                ) else null
            }.toList()

            context.respond(results)
        }
    }
}

private fun getLiveUpdateFromResultSet(connection: Connection, resultSet: ResultSet): LiveUpdate {
    val liveUpdate = LiveUpdate(
        title = resultSet.getString("title"),
        date = Date(resultSet.getTimestamp("date").time),
        category = resultSet.getString("category"),
        link = resultSet.getString("link"),
        description = resultSet.getString("description"),
        guid = resultSet.getString("guid"),
        featuredImageUrl = resultSet.getString("featured_image_url")
    )

    val stopsResult = connection.prepareStatement("SELECT * FROM live_updates_stops WHERE guid = ?").apply {
        setString(1, liveUpdate.guid)
    }.executeQuery()
    val stops =
        generateSequence { if (stopsResult.next()) getAffectedStopFromResultSet(stopsResult) else null }.toList()

    val routesResult = connection.prepareStatement("SELECT * FROM live_updates_routes WHERE guid = ?").apply {
        setString(1, liveUpdate.guid)
    }.executeQuery()
    val routes = generateSequence { if (routesResult.next()) routesResult.getString("route_number") else null }.toList()

    return liveUpdate.copy(affectedRoutes = routes, affectedStops = stops)
}

private fun getAffectedStopFromResultSet(resultSet: ResultSet): AffectedStop = AffectedStop(
    stop = resultSet.getString("stop_code"),
    alternateService = resultSet.getString("alternate_stop")
)