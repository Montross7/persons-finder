package com.persons.finder.repositories

import com.persons.finder.data.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long>{
    @Query(
        nativeQuery = true,
        value = """
        SELECT p FROM Person p 
        WHERE p.latitude BETWEEN :minLat AND :maxLat 
        AND p.longitude BETWEEN :minLong AND :maxLong
        """
    )
    fun findNearby(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLong") minLong: Double,
        @Param("maxLong") maxLong: Double,
        ): List<Person>
}