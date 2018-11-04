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

package ca.llamabagel.transpo.server.trips

import ca.llamabagel.transpo.models.trips.ApiResponse
import ca.llamabagel.transpo.models.trips.Route
import ca.llamabagel.transpo.models.trips.Trip
import ca.llamabagel.transpo.server.Keys
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.engine.apache.Apache
import io.ktor.client.response.readBytes
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

fun Routing.trips() {
    get("trips") {
        val stopCode = context.parameters["stop"]
        val client = HttpClient(Apache)
        val apiResponse = client.call("http://api.octranspo1.com/v1.2/GetNextTripsForStopAllRoutes?appID=" +
                "${Keys.OC_TRANSPO_APP_ID}&apiKey=${Keys.OC_TRANSPO_API_KEY}&stopNo=$stopCode")
                .response
                .readBytes()

        context.respond(buildResultFromResponse(stopCode, apiResponse))
    }
}

/**
 * Builds an [ApiResponse] object based on the (xml) response from a request to the OC Transpo API.
 *
 * @param response The string of the response from the OC Transpo API.
 * @return An [ApiResponse] object representing the computed form of the response from OC Transpo.
 */
private fun buildResultFromResponse(stopCode: String?, response: ByteArray): ApiResponse {
    // Check and enforce a valid non-null stop code (rest of API response would be an error anyway).
    stopCode ?: return ApiResponse("", 10, emptyList())

    val xPath = XPathFactory.newInstance().newXPath()
    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val document = documentBuilder.parse(ByteArrayInputStream(response))

    val responseRoot = xPath.evaluate("/Envelope/Body/GetRouteSummaryForStopResponse/GetRouteSummaryForStopResult",
            document,
            XPathConstants.NODE) as? Element
            ?: return ApiResponse(stopCode, -1, emptyList()) // Return error if node ended up not existing

    // Check for any errors returned from the API. TODO : Check error conditions
    val errorCode = (xPath.evaluate("./Error", responseRoot, XPathConstants.STRING) as? String)?.toIntOrNull()
    if (errorCode != null && errorCode != 0) {
        return ApiResponse(stopCode, errorCode, emptyList())
    }

    val routes = ArrayList<Route>()
    val inactiveRoutes = ArrayList<Route>()

    val routeNodes = xPath.evaluate("./Routes/Route", responseRoot, XPathConstants.NODESET) as NodeList
    for (i in 0 until routeNodes.length) {
        val route = buildRouteFromElement(routeNodes[i] as Element, xPath)

        // If a route is returned but has no trips, then it is considered "inactive"
        if (route.trips.isNotEmpty()) {
            routes.add(route)
        } else {
            inactiveRoutes.add(route)
        }
    }

    return ApiResponse(stopCode, 0, routes)
}

/**
 * Builds a [Route] object given the Route element from the XML response.
 * Extracts all of the required details including this route's list of [Trip]s.
 *
 * @param element The DOM Element for this route from the API response.
 * @return A [Route] object for this trip.
 */
private fun buildRouteFromElement(element: Element, xPath: XPath): Route {
    val number = xPath.evaluate("./RouteNo", element, XPathConstants.STRING) as String
    val directionId = (xPath.evaluate("./DirectionID", element, XPathConstants.STRING) as String).toInt()
    val direction = xPath.evaluate("./Direction", element, XPathConstants.STRING) as String
    val heading = xPath.evaluate("./RouteHeading", element, XPathConstants.STRING) as String
    val tripsElements = xPath.evaluate("./Trips/Trip", element, XPathConstants.NODESET) as NodeList

    val trips = ArrayList<Trip>()
    for (i in 0 until tripsElements.length) {
        trips.add(buildTripFromElement(tripsElements[i] as Element, xPath))
    }

    return Route(number, directionId, direction, heading, trips)
}

/**
 * Builds a [Trip] object given the Trip element from the XML response.
 * Extracts all of the required details including a readable [Trip.busType] string and [Trip.hasBikeRack].
 *
 * Note: At this time, this function does not calculate the trip's [Trip.punctuality].
 *
 * @param element The DOM Element for this trip from the API response.
 * @return A [Trip] object for this trip.
 */
private fun buildTripFromElement(element: Element, xPath: XPath): Trip {
    val tripDestination = xPath.evaluate("./TripDestination", element, XPathConstants.STRING) as String
    val tripStartTime = xPath.evaluate("./TripStartTime", element, XPathConstants.STRING) as String
    val adjustedScheduleTime = (xPath.evaluate("./AdjustedScheduleTime", element, XPathConstants.STRING) as String).toInt()
    val adjustmentAge = (xPath.evaluate("./AdjustmentAge", element, XPathConstants.STRING) as String).toFloat()
    val lastTripOfSchedule = (xPath.evaluate("./LastTripOfSchedule", element, XPathConstants.STRING) as String).toLowerCase() == "1"
    val busTypeText = xPath.evaluate("./BusType", element, XPathConstants.STRING) as String
    val latitudeText = xPath.evaluate("./Latitude", element, XPathConstants.STRING) as String
    val longitudeText = xPath.evaluate("./Longitude", element, XPathConstants.STRING) as String
    val gpsText = xPath.evaluate("./GPSSpeed", element, XPathConstants.STRING) as String

    return Trip(tripDestination,
            tripStartTime,
            adjustedScheduleTime,
            adjustmentAge,
            lastTripOfSchedule,
            busType = getBusTypeFromString(busTypeText),
            latitude = latitudeText.toDoubleOrNull(),
            longitude = longitudeText.toDoubleOrNull(),
            gpsSpeed = gpsText.toFloatOrNull(),
            hasBikeRack = busTypeText.contains("B"),
            punctuality = 0)
}

/**
 * Converts the "BusType" string returned by the OC Transpo API into a readable letter code.
 *
 * Example of OC Transpo's strings:
 * "6EB - 60", "4E - DEH", "4L - DEH", "4LB - IN", "DD - DEH", etc.
 *
 * The symbols mean the following:
 * * 4 or 40 = 40-foot bus
 * * 6 or 60 = 60-foot bus
 * * 4 and 6 = Either a 40 or 60 foot bus
 * * DD = Double Decker
 * * E, L, A, EA = Low floor easy access
 * * B = Bike Rack
 * * DEH = Diesel Electric Hybrid
 * * IN = New Flyer Inviro (40-foot bus)
 * * ON = Orion Bus (usually a hybrid)
 *
 * Note that in the case of DD-DEH, this tends to signify an "extra". And extra is just an extra bus that is
 * assigned by OC Tranpo during rush hours to handle extra trips. More often than not, it is not a Double Decker.
 *
 * @param typeString The "BusType" string from the OC Transpo API.
 * @return The readable letter code for that bus type. Either "S", "L", "H", or "DD".
 */
private fun getBusTypeFromString(typeString: String): String {
    return when {
        typeString.contains("ON") || typeString.contains("H") && !typeString.contains("DD") -> "H"
        typeString.contains("6") -> "L"
        typeString.contains("4") -> "S"
        typeString.contains("DD") && !typeString.contains("DEH") -> "DD"
        else -> ""
    }
}

/**
 * Subscript operator for the [NodeList] class.
 * More convenient to use than the [NodeList.item] method.
 *
 * @param i The index into the collection.
 */
private operator fun NodeList.get(i: Int) = item(i)