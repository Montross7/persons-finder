package com.persons.finder.repositories

import com.persons.finder.data.Person
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT * FROM person 
            WHERE (
                6371 * acos(
                    cos(radians(:queryLatitude)) 
                    * cos(radians(latitude)) 
                    * cos(radians(longitude) - radians(:queryLongitude)) 
                    + sin(radians(:queryLatitude)) 
                    * sin(radians(latitude))
                )
            ) <= :radiusKm and id != :id
            ORDER BY (
                6371 * acos(
                    cos(radians(:queryLatitude)) 
                    * cos(radians(latitude)) 
                    * cos(radians(longitude) - radians(:queryLongitude)) 
                    + sin(radians(:queryLatitude)) 
                    * sin(radians(latitude))
                )
            )
        """
    )
    fun findNearby(
        @Param("queryLatitude") queryLatitude: Double,
        @Param("queryLongitude") queryLongitude: Double,
        @Param("radiusKm") radiusKm: Double,
        @Param("id") id: Long,
        pageable: Pageable
    ): Page<Person>
}