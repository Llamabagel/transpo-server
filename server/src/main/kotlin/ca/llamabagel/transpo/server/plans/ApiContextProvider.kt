/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
 */

package ca.llamabagel.transpo.server.plans

import ca.llamabagel.transpo.server.Keys
import com.google.maps.GeoApiContext

object ApiContextProvider {
    val apiContext: GeoApiContext by lazy {
        GeoApiContext.Builder()
                .apiKey(Keys.GOOGLE_API_KEY)
                .build()
    }
}