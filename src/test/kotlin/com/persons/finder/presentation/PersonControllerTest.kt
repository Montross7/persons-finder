package com.persons.finder.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.dto.CreatePersonRequest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PersonController::class)
class PersonControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var personsService: PersonsService

    @Test
    fun `POST persons should return 201 CREATED with person ID`() {
        // Arrange
        val request = CreatePersonRequest(
            name = "John Doe",
            jobTitle = "Software Engineer",
            hobbies = listOf("coding", "gaming"),
            location = Location(latitude = -41.2865, longitude = 174.7762)
        )
        val savedPerson = Person(
            id = 1L,
            name = "John Doe",
            bio = "A quirky software engineer who enjoys coding and gaming.",
            jobTitle = "Software Engineer",
            hobbies = "[coding, gaming]",
            latitude = -41.2865,
            longitude = 174.7762
        )

        whenever(personsService.save(any(), any(), any(), any())).thenReturn(savedPerson)

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().string("1"))
    }

    @Test
    fun `POST persons with invalid data should return 400 BAD REQUEST`() {
        // Arrange - empty name
        val invalidRequest = """
            {
                "name": "",
                "jobTitle": "Engineer",
                "hobbies": ["coding"],
                "location": {"latitude": -41.2865, "longitude": 174.7762}
            }
        """

        // Act & Assert
        mockMvc.perform(
            post("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET nearby should return list of persons within radius`() {
        // Arrange
        val person1 = Person(1L, "Alice", "Bio1", "Engineer", "[reading]", -41.28, 174.77)
        val person2 = Person(2L, "Bob", "Bio2", "Designer", "[art]", -41.29, 174.78)
        val page = PageImpl(listOf(person1, person2))

        whenever(personsService.findAllAround(any(), any(), any<Pageable>())).thenReturn(page)

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("id", "1")
                .param("radius", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content.length()").value(2))
    }

    @Test
    fun `GET nearby with invalid radius should return 400`() {
        mockMvc.perform(
            get("/api/v1/persons/nearby")
                .param("id", "1")
                .param("radius", "0")  // Invalid: less than 1
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `PUT location should update and return person`() {
        // Arrange
        val updatedPerson = Person(1L, "John", "Bio", "Engineer", "[coding]", -40.0, 175.0)
        whenever(personsService.updateLocation(any(), any(), any())).thenReturn(updatedPerson)

        // Act & Assert
        mockMvc.perform(
            put("/api/v1/persons/1/location")
                .param("longitude", "175.0")
                .param("latitude", "-40.0")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.latitude").value(-40.0))
            .andExpect(jsonPath("$.longitude").value(175.0))
    }

    @Test
    fun `PUT location with invalid coordinates should return 400`() {
        mockMvc.perform(
            put("/api/v1/persons/1/location")
                .param("longitude", "200")  // Invalid: exceeds max 180
                .param("latitude", "-40.0")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `GET persons by ids should return name map`() {
        // Arrange
        val person1 = Person(1L, "Alice", "Bio1", "Engineer", "[reading]", -41.28, 174.77)
        val person2 = Person(2L, "Bob", "Bio2", "Designer", "[art]", -41.29, 174.78)
        
        whenever(personsService.getById(1L)).thenReturn(person1)
        whenever(personsService.getById(2L)).thenReturn(person2)

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/persons")
                .param("ids", "1", "2")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.1").value("Alice"))
            .andExpect(jsonPath("$.2").value("Bob"))
    }

    @Test
    fun `GET person by non-existent id should return 404`() {
        // Arrange
        whenever(personsService.getById(999L)).thenThrow(NoSuchElementException("Person not found"))

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/persons")
                .param("ids", "999")
        )
            .andExpect(status().isNotFound)
    }
}
