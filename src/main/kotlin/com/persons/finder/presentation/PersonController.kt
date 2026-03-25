package com.persons.finder.presentation

import com.persons.finder.data.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.CreatePersonRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "Persons", description = "Geo-location based person search with AI-generated bios")
class PersonController @Autowired constructor(
    private val personsService: PersonsService,
) {
    private val logger = LoggerFactory.getLogger(PersonController::class.java)

    @Operation(summary = "Update person location", description = "Update a person's geographic coordinates")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Location updated successfully"),
        ApiResponse(responseCode = "400", description = "Invalid coordinates (latitude: -90 to 90, longitude: -180 to 180)"),
        ApiResponse(responseCode = "404", description = "Person not found")
    ])
    @PutMapping("{id}/location")
    fun updateLocation(
        @Parameter(description = "Person ID", required = true) @PathVariable @Positive id: Long,
        @Parameter(description = "Longitude (-180 to 180)", required = true) @RequestParam("longitude") @Min(-180) @Max(180) longitude: Double,
        @Parameter(description = "Latitude (-90 to 90)", required = true) @RequestParam("latitude") @Min(-90) @Max(90) latitude: Double
    ): ResponseEntity<Person> {
        val updatedPerson = personsService.updateLocation(id, longitude, latitude)
        return ResponseEntity.ok(updatedPerson)
    }

    @Operation(summary = "Create new person", description = "Create a person with AI-generated bio using local Ollama LLM")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Person created successfully, returns ID"),
        ApiResponse(responseCode = "400", description = "Invalid input data")
    ])
    @PostMapping("")
    fun generateNewPerson(@Valid @RequestBody request: CreatePersonRequest): ResponseEntity<Long> {
        val response = personsService.save(request.name, request.jobTitle, request.hobbies, request.location)
        return ResponseEntity.status(HttpStatus.CREATED).body(response.id)
    }

    @Operation(summary = "Find nearby persons", description = "Find people within a radius using Haversine formula for geo-search")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returns paginated list of nearby persons"),
        ApiResponse(responseCode = "400", description = "Invalid ID or radius"),
        ApiResponse(responseCode = "404", description = "Person not found")
    ])
    @GetMapping("nearby")
    fun nearbyPersons(
        @Parameter(description = "Reference person ID", required = true) @RequestParam("id") @Positive id: Long,
        @Parameter(description = "Search radius in kilometers (min: 1)", required = true) @RequestParam(value = "radius") @Min(value = 1, message = "Radius must be at least 1km") radius: Double
    ): ResponseEntity<*> {
        val personList = personsService.findAllAround(id, radius, PageRequest.of(0, 10))
        return ResponseEntity.ok(personList)
    }

    @Operation(summary = "Get person names", description = "Retrieve names for multiple persons by their IDs")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Returns map of ID to name"),
        ApiResponse(responseCode = "404", description = "One or more persons not found")
    ])
    @GetMapping("")
    fun getPersonName(
        @Parameter(description = "List of person IDs", required = true) @RequestParam("ids") ids: List<Long>
    ): ResponseEntity<Map<Long, String>> {
        val personNames = ids.associate { id -> 
            id to personsService.getById(id).name
        }
        return ResponseEntity.ok(personNames)
    }

}