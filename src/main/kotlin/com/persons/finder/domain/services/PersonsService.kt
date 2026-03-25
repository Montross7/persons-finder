package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PersonsService {
    fun getById(id: Long): Person
    fun save(name: String, jobTitle: String, hobbies: List<String>, location: Location): Person

    fun updateLocation(id: Long, longitude: Double, latitude: Double): Person

    fun findAllAround(id: Long, radius: Double, pageable: Pageable): Page<Person>
}