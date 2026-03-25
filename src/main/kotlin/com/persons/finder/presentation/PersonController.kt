package com.persons.finder.presentation

import com.persons.finder.data.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.CreatePersonRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Positive

@RestController
@RequestMapping("api/v1/persons")
@Validated
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
) {
    private val logger = LoggerFactory.getLogger(PersonController::class.java)

    /*
        TODO PUT API to update/create someone's location using latitude and longitude
        (JSON) Body
     */
    @PutMapping("{id}/location")
    fun updateLocation(
        @PathVariable @Positive id: Long,
        @RequestParam("longitude") @Min(-180) @Max(180) longitude: Double,
        @RequestParam("latitude") @Min(-90) @Max(90) latitude: Double
    ): ResponseEntity<Person> {
        val updatedPerson = personsService.updateLocation(id, longitude, latitude)
        return ResponseEntity.ok(updatedPerson)
    }

    /*
        TODO POST API to create a 'person'
        (JSON) Body and return the id of the created entity
    */
    @PostMapping("")
    fun generateNewPerson(@Valid @RequestBody request: CreatePersonRequest): ResponseEntity<Long> {
        val response = personsService.save(request.name, request.jobTitle, request.hobbies, request.location)
        return ResponseEntity.status(HttpStatus.CREATED).body(response.id)
    }

    /*
        TODO GET API to retrieve people around query location with a radius in KM, Use query param for radius.
        TODO API just return a list of persons ids (JSON)
        // Example
        // John wants to know who is around his location within a radius of 10km
        // API would be called using John's id and a radius 10km
     */
    @GetMapping("nearby")
    fun nearbyPersons(
        @RequestParam("id") @Positive id: Long,
        @RequestParam(value = "radius") @Min(value = 1, message = "Radius must be at least 1km") radius: Double
    ): ResponseEntity<*> {
        val personList = personsService.findAllAround(id, radius, PageRequest.of(0, 10))
        return ResponseEntity.ok(personList)
    }

    /*
        TODO GET API to retrieve a person or persons name using their ids
        // Example
        // John has the list of people around them, now they need to retrieve everybody's names to display in the app
        // API would be called using person or persons ids
     */
    @GetMapping("")
    fun getPersonName(@RequestParam("ids") ids: List<Long>): ResponseEntity<Map<Long, String>> {
        val personNames = ids.associate { id -> 
            id to personsService.getById(id).name
        }
        return ResponseEntity.ok(personNames)
    }

}