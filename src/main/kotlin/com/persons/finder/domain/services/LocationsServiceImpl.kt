package com.persons.finder.domain.services

import com.persons.finder.data.Location
import org.springframework.stereotype.Service

@Service
class LocationsServiceImpl() : LocationsService {

    override fun addLocation(location: Location) {
    }

    override fun removeLocation(locationReferenceId: Long) {
    }

    override fun findAround(latitude: Double, longitude: Double, radiusInKm: Double): List<Location> {
        TODO("Not yet implemented")
    }

}