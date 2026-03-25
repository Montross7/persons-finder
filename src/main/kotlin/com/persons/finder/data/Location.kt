package com.persons.finder.data

import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class Location(
    @field:Min(-90) @field:Max(90)
    val latitude: Double,
    @field:Min(-180) @field:Max(180)
    val longitude: Double
)
