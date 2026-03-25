package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.repositories.PersonRepository
import org.springframework.stereotype.Service

@Service
class PersonsServiceImpl(
    private val personRepository: PersonRepository,
    private val bioGeneratorService: BioGeneratorService
) : PersonsService {

    override fun getById(id: Long): Person {
        return personRepository.findById(id).get()
    }

    override fun save(name: String, jobTitle: String, hobbies: List<String>, location: Location): Person {
        val newBio = bioGeneratorService.generateBio(jobTitle, hobbies)
        return personRepository.save(
            Person(
                id = 0,
                name,
                jobTitle = jobTitle,
                hobbies = hobbies.toString(),
                bio = newBio,
                longitude = location.longitude,
                latitude = location.latitude
            )
        )

    }

    override fun updateLocation(id: Long, longitude: Double, latitude: Double): Person {
        val updatedPerson = personRepository.findById(id).orElseThrow()
        updatedPerson.longitude = longitude
        updatedPerson.latitude = latitude
        return personRepository.save(updatedPerson)
    }

    override fun findAllAround(
        id: Long,
        radius: Double
    ): List<Person> {
        val currentPerson = personRepository.findById(id).orElseThrow()
        val minLatitude =
        return personRepository.findNearby()
    }


}