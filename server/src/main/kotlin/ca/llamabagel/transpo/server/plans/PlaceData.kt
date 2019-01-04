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

package ca.llamabagel.transpo.server.plans

import ca.llamabagel.transpo.models.LatLng
import ca.llamabagel.transpo.server.Keys
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.PlacesApi
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import io.ktor.client.response.readText

/**
 * A summary of several pieces of information about a place to be used in getting a travel plan to/from that place.
 * These pieces of data are all needed and used by the travel plan API to generate the travel plans.
 *
 * Usually the submitted [ca.llamabagel.transpo.models.plans.request.PlansRequest] provides a subclass of [ca.llamabagel.transpo.models.plans.request.Location] but
 * these subclasses do not contain all three pieces of data. To get the missing pieces of data, use either
 * [getPlaceDataFromId] or [getPlaceDataFromLatLng].
 *
 * @property identifier The Google Places identifier for this place
 * @property description A human-readable description of this place. Either the name of the building or its address.
 * @property location The [LatLng] of this place.
 */
data class PlaceData(val identifier: String,
                     val description: String,
                     val location: LatLng)

/**
 * Gets the data for a [PlaceData] object from a [LatLng] using Google's reverse geocoding API.
 *
 * @param latLng The LatLng of the location.
 * @param overwriteDescription Whether or not to overwrite the description of the place using the data returned from the API.
 * If false, the description of the PLaceData will be the description argument.
 * @param description The description of the place to use if not overwriting it with the result from the API.
 * @return A complete [PlaceData] object, or null if an error occurred.
 */
suspend fun getPlaceDataFromLatLng(latLng: LatLng, overwriteDescription: Boolean = true, description: String = ""): PlaceData? {
    val geocodeResult = try {
        GeocodingApi
                .reverseGeocode(ApiContextProvider.apiContext, com.google.maps.model.LatLng(latLng.latitude, latLng.longitude))
                .await()
    } catch (e: Exception) {
        return null
    }

    val result = geocodeResult[0]
    return PlaceData(result.placeId, if (overwriteDescription) result.addressComponents[0].longName else description, latLng)
}

/**
 * Gets the data for a [PlaceData] object from a place ID using Google's Place Details API.
 *
 * @param placeId The identifier for the place.
 * @param overwriteDescription Whether or not to overwrite the description of the place using the data returned from the API.
 * If false, the description of the PLaceData will be the description argument.
 * @param description The description of the place to use if not overwriting it with the result from the API.
 * @return A complete [PlaceData] object, or null if an error occurred.
 */
suspend fun getPlaceDataFromId(placeId: String, overwriteDescription: Boolean = true, description: String = ""): PlaceData? {
    val detailsResult = try {
        PlacesApi.placeDetails(ApiContextProvider.apiContext, placeId).await()
    } catch (e: Exception) {
        return null
    }

    val latLng = LatLng(detailsResult.geometry.location.lat, detailsResult.geometry.location.lng)
    return PlaceData(placeId, if (overwriteDescription) detailsResult.name else description, latLng)
}