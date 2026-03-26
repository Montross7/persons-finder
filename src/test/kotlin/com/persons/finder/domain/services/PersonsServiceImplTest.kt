package com.persons.finder.domain.services

import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.repositories.PersonRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class PersonsServiceImplTest {

    @Mock
    private lateinit var personRepository: PersonRepository

    @Mock
    private lateinit var bioGeneratorService: BioGeneratorService

    @InjectMocks
    private lateinit var personsService: PersonsServiceImpl

    @Test
    fun `getById should return person when exists`() {
        // Arrange
        val person = Person(1L, "John", "Bio", "Engineer", "[coding]", -41.0, 174.0)
        whenever(personRepository.findById(1L)).thenReturn(Optional.of(person))

        // Act
        val result = personsService.getById(1L)

        // Assert
        assertEquals(person, result)
        verify(personRepository).findById(1L)
    }

    @Test
    fun `getById should throw exception when not found`() {
        // Arrange
        whenever(personRepository.findById(999L)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(NoSuchElementException::class.java) {
            personsService.getById(999L)
        }
    }

    @Test
    fun `updateLocation should modify latitude and longitude`() {
        // Arrange
        val person = Person(1L, "John", "Bio", "Engineer", "[coding]", -41.0, 174.0)
        
        whenever(personRepository.findById(1L)).thenReturn(Optional.of(person))
        doAnswer { invocation ->
            invocation.arguments[0] as Person
        }.whenever(personRepository).save(any())

        // Act
        val result = personsService.updateLocation(1L, 175.0, -40.0)

        // Assert
        assertEquals(-40.0, result.latitude)
        assertEquals(175.0, result.longitude)
        verify(personRepository).save(any())
    }

    @Test
    fun `save should generate bio and persist person`() {
        // Arrange
        val name = "Alice"
        val jobTitle = "Data Scientist"
        val hobbies = listOf("reading", "hiking")
        val location = Location(-41.0, 174.0)
        val generatedBio = "A quirky data scientist who loves reading and hiking."
        
        whenever(bioGeneratorService.generateBio(jobTitle, hobbies)).thenReturn(generatedBio)
        doAnswer { invocation ->
            invocation.arguments[0] as Person
        }.whenever(personRepository).save(any())

        // Act
        val result = personsService.save(name, jobTitle, hobbies, location)

        // Assert
        assertEquals(name, result.name)
        assertEquals(generatedBio, result.bio)
        assertEquals(jobTitle, result.jobTitle)
        verify(bioGeneratorService).generateBio(jobTitle, hobbies)
        verify(personRepository).save(any())
    }
}
