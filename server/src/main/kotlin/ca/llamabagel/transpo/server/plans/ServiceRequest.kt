/*
 * Copyright (c) 2019 Llamabagel.
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

import com.google.maps.model.LatLng

class ServiceRequest(var origin: Place,
                     var destination: Place,
                     var allowBike: Boolean = false,
                     var bikeAtArrivalDeparture: String = "None",
                     var considerBus: Boolean = true,
                     var considerEvent: Boolean = true,
                     var considerExpress: Boolean = true,
                     var considerFlexi: Boolean = true,
                     var considerMetro: Boolean = true,
                     var considerNight: Boolean = true,
                     var considerRegular: Boolean = true,
                     var considerSchool: Boolean = true,
                     var considerTrain: Boolean = true,
                     var considerTramway: Boolean = true,
                     var date: String = "y-M-d",
                     var excludedSites: String = "",
                     var maxBikeDistance: Double = 0.0,
                     var needAccessibility: Boolean = true,
                     var parkingAtArrivalDeparture: String = "None",
                     var requestTimeType: String = "SpecifiedDepartureTime",
                     var time: String = "HH:mm",
                     var viaLocation: String = "") {

    class Place(val description: String,
                val location: Location)

    class Location(val civicNumber: String,
                   val identifier: String,
                   val longLat: LatLng,
                   val streetName: String,
                   val type: String)

    companion object {
        const val TIME_TYPE_DEPART_AT = "SpecifiedDepartureTime"
        const val TIME_TYPE_ARRIVE_AT = "SpecifiedArrivalTime"
    }

}