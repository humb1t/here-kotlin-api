package com.github.humb1t.here

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils

class MapImage(HERE_APP_ID: String, HERE_APP_CODE: String) {
    val BASE_URL = "https://image.maps.api.here.com/mia/1.6/mapview?app_id=${HERE_APP_ID}&app_code=${HERE_APP_CODE}"

    fun defaultLocation(): ByteArray? {
        return EntityUtils.toByteArray(
            Request.Get(BASE_URL)
                .execute()
                .returnResponse()
                .entity
        )
    }

    fun zoomedLocation(lat: Float, long: Float, zoom: Byte): ByteArray? {
        return EntityUtils.toByteArray(
            Request.Get("$BASE_URL&c=$lat,$long&z=$zoom")
                .execute()
                .returnResponse()
                .entity
        )
    }

    fun zoomedUncertainLocation(lat: Float, long: Float, zoom: Byte, uncertainty: String): ByteArray? {
        return EntityUtils.toByteArray(
            Request.Get("$BASE_URL&c=$lat,$long&z=$zoom&u=$uncertainty")
                .execute()
                .returnResponse()
                .entity
        )
    }
}

class Geocode(HERE_APP_ID: String, HERE_APP_CODE: String) {
    val BASE_URL = "https://geocoder.api.here.com/6.2/geocode.json?app_id=${HERE_APP_ID}&app_code=${HERE_APP_CODE}"
    val objectMapper = ObjectMapper().registerModule(KotlinModule())
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

    fun findCoordinates(address: String): GeocodeResponse {
        return objectMapper.readValue<GeocodeResponse>(
            Request.Get("$BASE_URL&searchtext=$address")
                .execute()
                .returnResponse()
                .entity
                .content,
            GeocodeResponse::class.java
        )
    }

    data class GeocodeResponse(
        val response: Response
    )

    data class Response(
        val metaInfo: MetaInfo,
        val view: List<View>
    )

    data class View(
        val _type: String,
        val viewId: Int,
        val result: List<Result>
    )

    data class Result(
        val relevance: Int,
        val matchLevel: String,
        val matchQuality: MatchQuality,
        val matchType: String,
        val location: Location
    )

    data class MatchQuality(
        val city: Int,
        val street: List<Double>,
        val houseNumber: Int
    )

    data class Location(
        val locationId: String,
        val locationType: String,
        val displayPosition: DisplayPosition,
        val navigationPosition: List<NavigationPosition>,
        val mapView: MapView,
        val address: Address
    )

    data class NavigationPosition(
        val latitude: Double,
        val longitude: Double
    )

    data class MapView(
        val topLeft: TopLeft,
        val bottomRight: BottomRight
    )

    data class TopLeft(
        val latitude: Double,
        val longitude: Double
    )


    data class BottomRight(
        val latitude: Double,
        val longitude: Double
    )


    data class DisplayPosition(
        val Latitude: Double,
        val Longitude: Double
    )


    data class Address(
        val Label: String,
        val Country: String,
        val State: String,
        val County: String,
        val City: String,
        val District: String,
        val Street: String,
        val HouseNumber: String,
        val PostalCode: String,
        val AdditionalData: List<AdditionalData>
    )


    data class AdditionalData(
        val value: String,
        val key: String
    )


    data class MetaInfo(
        val timestamp: String
    )
}

class Places(HERE_APP_ID: String, HERE_APP_CODE: String) {
    val BASE_URL =
        "https://places.cit.api.here.com/places/v1/autosuggest?app_id=${HERE_APP_ID}&app_code=${HERE_APP_CODE}"
    val objectMapper = ObjectMapper().registerModule(KotlinModule())
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

    fun find(lat: Float, long: Float, query: String): Response? {
        return objectMapper.readValue<Response>(
            Request.Get("$BASE_URL&at=$lat,$long&q=$query")
                .execute()
                .returnResponse()
                .entity
                .content,
            Response::class.java
        )
    }


    data class Response(
        val results: List<Result>
    )


    data class Result(
        val title: String,
        val highlightedTitle: String,
        val vicinity: String?,
        val position: List<Double>?,
        val category: String?,
        val href: String,
        val type: String
    )
}
