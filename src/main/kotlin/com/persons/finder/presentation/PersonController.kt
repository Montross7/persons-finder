package com.persons.finder.presentation

import com.persons.finder.data.Person
import com.persons.finder.domain.services.BioGeneratorService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.CreatePersonRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/persons")
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
) {
    private val logger = LoggerFactory.getLogger(BioGeneratorService::class.java)

    /*
        TODO PUT API to update/create someone's location using latitude and longitude
        (JSON) Body
     */
    @PutMapping("{id}/location")
    fun updateLocation(
        @RequestParam("id") id: Long,
        @RequestParam("longitude") longitude: Double,
        @RequestParam("latitude") latitude: Double
    ): String {
        personsService.updateLocation(
            id, longitude, latitude
        )
        return "SUCCESS"
    }

    /*
        TODO POST API to create a 'person'
        (JSON) Body and return the id of the created entity
    */
    @PostMapping("")
    fun generateNewPerson(@RequestBody request: CreatePersonRequest): Long {
        val response = personsService.save(request.name, request.jobTitle, request.hobbies, request.location)
        return response.id
    }

    /*
        TODO GET API to retrieve people around query location with a radius in KM, Use query param for radius.
        TODO API just return a list of persons ids (JSON)
        // Example
        // John wants to know who is around his location within a radius of 10km
        // API would be called using John's id and a radius 10km
     */
    @GetMapping("nearby")
    fun nearbyPersons(@RequestParam("id") id: Long, @RequestParam(value = "radius") radius: Double) : List<Person> {
        logger.info("Fetching nearby persons for $id")
        val personList = personsService.findAllAround(id, radius)
        return personList
    }

    /*
        TODO GET API to retrieve a person or persons name using their ids
        // Example
        // John has the list of people around them, now they need to retrieve everybody's names to display in the app
        // API would be called using person or persons ids
     */
    @GetMapping("")
    fun getPersonName(@RequestParam("ids") ids: List<Long>): String {
        return "Test"
    }

}