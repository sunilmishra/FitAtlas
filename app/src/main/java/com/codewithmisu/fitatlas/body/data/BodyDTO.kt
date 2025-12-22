package com.codewithmisu.fitatlas.body.data

data class BodyPartResponseDTO(
    val success: Boolean,
    val data: List<BodyPartDTO>
)

data class BodyPartDTO(
    val name: String,
    val imageUrl: String
)