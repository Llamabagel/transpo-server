/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.feed

import ca.llamabagel.transpo.models.updates.LiveUpdate
import ca.llamabagel.transpo.server.DataSource
import ca.llamabagel.transpo.server.utils.CoroutinesDispatcherProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class LiveUpdatesCacher(
    private val periodMinutes: Long = 5,
    private val provider: LiveUpdateFeedProvider = SimpleFeedProvider(),
    private val dispatchers: CoroutinesDispatcherProvider = CoroutinesDispatcherProvider()
) :
    LiveUpdateFeedProvider by provider {
    private var scheduledFuture: ScheduledFuture<*>? = null

    /**
     * Begins periodically fetching LiveUpdates and caching them in the database.
     */
    fun beginUpdates() {
        val executorService = Executors.newSingleThreadScheduledExecutor()
        scheduledFuture =
            executorService.scheduleAtFixedRate(::cacheResults, 0, periodMinutes, TimeUnit.MINUTES)
    }

    /**
     * Cancels any ongoing updates of the LiveData cache and any future scheduled updates.
     */
    fun cancelUpdates() {
        scheduledFuture?.cancel(true)
        scheduledFuture = null
    }

    private fun cacheResults() {
        GlobalScope.launch {
            val allItems = Language.values().flatMap { language ->
                val feed = getFeed("http://www.octranspo.com/$language/feeds/updates-$language/")
                insertItems(language, feed)

                return@flatMap feed
            }

            updateCancelledItems(allItems)
        }
    }

    private suspend fun insertItems(language: Language, items: List<LiveUpdate>) = withContext(dispatchers.io) {
        DataSource.getConnection().use { connection ->

            // Get active updates
            val existing = connection.prepareStatement("SELECT * FROM live_updates WHERE active = true").executeQuery()
                .use { resultSet ->
                    generateSequence {
                        if (resultSet.next()) getLiveUpdateFromResultSet(resultSet) else null
                    }.toList()
                }

            connection.prepareStatement("INSERT INTO live_updates VALUES(?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
                .use { statement ->
                    // Only insert items that aren't already in the database
                    items
                        .filter { item -> existing.find { item.guid == it.guid } == null }
                        .forEach { item ->
                            val (title, date, category, link, description, guid, _, _, featuredImageUrl) = item
                            with(statement) {
                                setString(1, guid)
                                setString(2, language.string)
                                setString(3, title)
                                setTimestamp(4, Timestamp(date.toInstant().toEpochMilli()))
                                setString(5, category)
                                setString(6, link)
                                setString(7, description)
                                setString(8, featuredImageUrl)
                            }
                            statement.execute()
                            insertLiveUpdateAffected(connection, item)
                        }
                }
        }
    }

    private fun updateCancelledItems(items: List<LiveUpdate>) {
        DataSource.getConnection().use { connection ->
            connection.prepareStatement("UPDATE live_updates SET active = false, removal_date = current_timestamp WHERE active = true AND guid NOT IN (${items.joinToString { "'${it.guid}'" }})")
                .use { statement ->
                    statement.execute()
                }
        }
    }

    private fun insertLiveUpdateAffected(connection: Connection, liveUpdate: LiveUpdate) {
        connection.prepareStatement("INSERT INTO live_updates_routes VALUES(?, ?)")
            .use { statement ->
                liveUpdate.affectedRoutes.forEach { route ->
                    with(statement) {
                        setString(1, liveUpdate.guid)
                        setString(2, route)
                    }
                    statement.execute()
                }
            }

        connection.prepareStatement("INSERT INTO live_updates_stops VALUES (?, ?, ?)")
            .use { statement ->
                liveUpdate.affectedStops.forEach { affectedStop ->
                    with(statement) {
                        setString(1, liveUpdate.guid)
                        setString(2, affectedStop.stop)
                        setString(3, affectedStop.alternateService)
                    }
                    statement.execute()
                }
            }
    }

    private fun getLiveUpdateFromResultSet(resultSet: ResultSet): LiveUpdate {
        return LiveUpdate(
            title = resultSet.getString("title"),
            date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(resultSet.getTimestamp("date").time), ZoneOffset.UTC),
            category = resultSet.getString("category"),
            link = resultSet.getString("link"),
            description = resultSet.getString("description"),
            guid = resultSet.getString("guid")
        )
    }
}

enum class Language(val string: String) {
    EN("en"),
    FR("fr");

    operator fun component1(): String = string

    override fun toString(): String = string
}