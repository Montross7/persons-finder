package com.persons.finder.presentation.dto

import com.persons.finder.data.Location

data class CreatePersonRequest(
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val location: Location
)