package com.persons.finder.presentation.dto

import com.persons.finder.data.Location
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class CreatePersonRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val jobTitle: String,
    @field:Size(max = 5)
    val hobbies: List<@NotBlank @Size(max = 50) String>,

    @field:Valid
    @field:NotNull
    val location: Location
)