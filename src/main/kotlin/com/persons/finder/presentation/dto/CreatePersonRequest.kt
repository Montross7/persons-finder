package com.persons.finder.presentation.dto

import com.persons.finder.data.Location
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreatePersonRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val jobTitle: String,
    @field:Size(max = 10)
    val hobbies: List<@NotBlank @Size(max = 50) String>,

    val location: Location
)