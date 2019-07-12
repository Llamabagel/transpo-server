/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.feed

import ca.llamabagel.transpo.models.updates.LiveUpdate

interface LiveUpdateFeedProvider {
    suspend fun getFeed(feedUrl: String): List<LiveUpdate>
}