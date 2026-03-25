package com.persons.finder.data

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Person(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val name: String,
    val bio: String,
    val jobTitle: String,
    val hobbies: String,

    var latitude: Double,
    var longitude: Double,
)
