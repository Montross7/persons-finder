package com.persons.finder.presentation.dto

import com.persons.finder.data.Location

data class UpdateLocationRequest(val id: Long, val location: Location)