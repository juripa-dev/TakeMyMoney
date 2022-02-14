package com.juripa.takemymoney.location

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.*


object LatLongDistance {

    private val PI_RAD = PI / 180.0
    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in meters.
     */
    fun greatCircleInFeet(latLng1: LatLng, latLng2: LatLng): Double {
        return greatCircleInKilometers(
            latLng1.latitude, latLng1.longitude, latLng2.latitude,
            latLng2.longitude
        ) * 3280.84
    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in meters.
     */
    fun greatCircleInMeters(latLng1: LatLng, latLng2: LatLng): Double {
        return greatCircleInKilometers(
            latLng1.latitude, latLng1.longitude, latLng2.latitude,
            latLng2.longitude
        ) * 1000
    }

    /**
     * Use Great Circle distance formula to calculate distance between 2 coordinates in kilometers.
     * https://software.intel.com/en-us/blogs/2012/11/25/calculating-geographic-distances-in-location-aware-apps
     */
    fun greatCircleInKilometers(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val phi1 = lat1 * PI_RAD
        val phi2 = lat2 * PI_RAD
        val lam1 = long1 * PI_RAD
        val lam2 = long2 * PI_RAD
        return 6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1))
    }
}