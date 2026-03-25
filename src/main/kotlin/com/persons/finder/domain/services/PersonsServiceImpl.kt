package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.repositories.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PersonsServiceImpl(
    private val personRepository: PersonRepository,
    private val bioGeneratorService: BioGeneratorService
) : PersonsService {

    private val logger = LoggerFactory.getLogger(PersonsServiceImpl::class.java)


    override fun getById(id: Long): Person {
        return personRepository.findById(id).orElseThrow()
    }

    override fun save(name: String, jobTitle: String, hobbies: List<String>, location: Location): Person {

        val newBio = bioGeneratorService.generateBio(jobTitle, hobbies)
        val generatedPerson = Person(
            id = 0,
            name,
            jobTitle = jobTitle,
            hobbies = hobbies.toString(),
            bio = newBio,
            longitude = location.longitude,
            latitude = location.latitude
        )
        logger.info("Saving $generatedPerson")
        return personRepository.save(
            generatedPerson

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
        radius: Double,
        pageable: Pageable
    ): Page<Person> {

        val currentPerson = personRepository.findById(id).orElseThrow()
        return personRepository.findNearby(
            currentPerson.latitude,
            currentPerson.longitude,
            radius,
            id,
            pageable
        )
    }


}