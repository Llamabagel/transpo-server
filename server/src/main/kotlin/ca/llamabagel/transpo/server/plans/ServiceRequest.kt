/*
 * Copyright (c) 2019 Derek Ellis. Subject to the MIT license.
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