package com.persons.finder.data

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Person(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val bio: String = "",
    val jobTitle: String = "",
    val hobbies: String = "",

    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)
